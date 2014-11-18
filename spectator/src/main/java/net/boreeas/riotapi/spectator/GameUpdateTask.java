/*
 * Copyright 2014 The LolDevs team (https://github.com/loldevs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi.spectator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.RequestException;
import net.boreeas.riotapi.spectator.rest.ChunkInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Pulls chunks and keyframes for the specified game from the server. Needs
 * to run several times, scheduling is left to the using classes. Setting
 * the running task via @see #setSelf(ScheduledFuture) will allow the task
 * to stop itself on error or completion.
 * Created 8/12/2014
 * @author Malte Schütze
 */
@Log4j
public class GameUpdateTask implements Runnable {

    private static final int DEFAULT_MAX_RETRIES = 5;

    @Getter private InProgressGame game;

    @Setter private ScheduledFuture self;
    private IntConsumer onChunkPulled;
    private IntConsumer onKeyframePulled;
    private Consumer<Exception> onError;
    private Callback onFinished;
    private IntConsumer onKeyframeFailed;
    private IntConsumer onChunkFailed;

    private boolean firstRun = true;
    private Map<Integer, Integer> retriesChunks = new HashMap<>();
    private Map<Integer, Integer> retriesKeyframes = new HashMap<>();

    public GameUpdateTask(InProgressGame game) {
        this(game, err -> {});
    }

    public GameUpdateTask(InProgressGame game, Consumer<Exception> errorCallback) {
        this.game = game;
        this.onError = errorCallback;
    }

    public void addOnChunkPulled(IntConsumer consumer) {
        this.onChunkPulled = onChunkPulled == null ? consumer : onChunkPulled.andThen(consumer);
    }

    public void addOnKeyframePulled(IntConsumer consumer) {
        this.onKeyframePulled = onKeyframePulled == null ? consumer : onKeyframePulled.andThen(consumer);
    }

    public void addOnError(Consumer<Exception> consumer) {
        this.onError = onError == null ? consumer : onError.andThen(consumer);
    }

    public void addOnFinished(Callback callback) {
        this.onFinished = callback.and(onFinished);
    }

    public void addOnKeyframeFailed(IntConsumer consumer) {
        this.onKeyframeFailed = onKeyframeFailed == null ? consumer : onKeyframeFailed.andThen(consumer);
    }

    public void addOnChunkFailed(IntConsumer consumer) {
        this.onChunkFailed = onChunkFailed == null ? consumer : onChunkFailed.andThen(consumer);
    }

    @Deprecated
    public void setOnError(Consumer<Exception> consumer) {
        onError = consumer;
    }

    @Deprecated
    public void setOnFinished(Callback callback) {
        onFinished = callback;
    }

    @Deprecated
    public void setOnChunkPulled(IntConsumer consumer) {
        this.onChunkPulled = consumer;
    }

    @Deprecated
    public void setOnKeyframePulled(IntConsumer consumer) {
        this.onKeyframePulled = consumer;
    }

    @Override
    public void run() {
        if (firstRun) {
            pullAllAvailable();
            firstRun = false;
            return;
        }

        try {
            updateNew();
        } catch (Exception ex) {
            onError.accept(ex);
        }
    }

    private void updateNew() {
        ChunkInfo chunkInfo = game.getLastChunkInfo();
        if (chunkInfo.getChunkId() < game.getLastAvailableChunk()) {
            cancelWithError(new IllegalStateException("Last available chunk id reverted (" + game.getLastAvailableChunk() + " -> " + chunkInfo.getChunkId() + ")"));
            return;
        } else if (chunkInfo.getChunkId() > game.getLastAvailableChunk()) {
            pullNewChunks(chunkInfo.getChunkId());
        }

        if (chunkInfo.getKeyFrameId() != game.getLastAvailableKeyFrame()) {
            pullNewKeyframes(chunkInfo);

        }

        doRetriesKeyframes();
        doRetriesChunk();

        if (chunkInfo.getEndGameChunkId() != 0 && chunkInfo.getEndGameChunkId() == chunkInfo.getChunkId() && retriesKeyframes.isEmpty() && retriesChunks.isEmpty()) {
            log.debug("[" + game.getGameId() + "] End of game reached at chunk " + chunkInfo.getEndGameChunkId());
            game.markEndReached();
            if (onFinished != null) { onFinished.receive(); }
            cancel();
        }
    }

    public void cancel() {
        if (self != null) self.cancel(false);
    }

    private void cancelWithError(Exception ex) {
        onError.accept(ex);
        cancel();
    }

    private void pullNewKeyframes(ChunkInfo chunkInfo) {
        // Since we update every chunk, and keyframes don't appear as often as chunks,
        // assume at most one new keyframe
        game.pullKeyFrame(chunkInfo.getKeyFrameId());
    }

    private void pullNewChunks(int maxId) {
        //log.debug("[" + game.getGameId() + "] " + (maxId - game.getLastAvailableChunk()) + " new chunks (" + (game.getLastAvailableChunk() + 1) + " to " + maxId + ")");

        // Time between chunks is occasionally less than the chunk time interval
        // So we check for all missed chunks here
        for (int id = game.getLastAvailableChunk() + 1; id <= maxId; id++) {
            pullChunk(id);
        }
    }

    private void doRetriesKeyframes() {
        Set<Integer> retriesExceeded = new HashSet<>();
        Set<Integer> retrySuccessful = new HashSet<>();
        for (Map.Entry<Integer, Integer> retryData: retriesKeyframes.entrySet()) {
            retryKeyframe(retriesExceeded, retrySuccessful, retryData.getKey(), retryData.getValue());
        }

        if (!retriesExceeded.isEmpty()) {
            for (int i: retriesExceeded) {
                retriesKeyframes.remove(i);
                if (onKeyframePulled != null) { onKeyframeFailed.accept(i); }
            }
        }

        if (!retrySuccessful.isEmpty()) {
            retrySuccessful.forEach(retriesKeyframes::remove);
        }
    }

    private void doRetriesChunk() {
        Set<Integer> retriesExceeded = new HashSet<>();
        Set<Integer> retrySuccessful = new HashSet<>();
        for (Map.Entry<Integer, Integer> retryData: retriesChunks.entrySet()) {
            retryChunk(retriesExceeded, retrySuccessful, retryData.getKey(), retryData.getValue());
        }

        if (!retriesExceeded.isEmpty()) {
            for (int i: retriesExceeded) {
                retriesChunks.remove(i);
                if (onChunkFailed != null) { onChunkFailed.accept(i); }
            }
        }

        if (!retrySuccessful.isEmpty()) {
            retrySuccessful.forEach(retriesChunks::remove);
        }
    }


    private void pullChunk(int id) {
        try {
            game.pullChunk(id);
            if (onChunkPulled != null) { onChunkPulled.accept(id); }
        } catch (RequestException ex) {
            if (ex.getErrorType() == RequestException.ErrorType.INTERNAL_SERVER_ERROR) {
                retriesChunks.put(id, 1);
            } else {
                throw ex;
            }
        }
    }


    private void retryChunk(Set<Integer> bad, Set<Integer> good, int id, int retries) {
        try {
            log.debug("[" + game.getGameId() + "] Reattempting to pull chunk " + id + " (attempt " + retries + "/" + DEFAULT_MAX_RETRIES + ")");
            game.pullChunk(id);
            good.add(id);   // Notify of success
        } catch (RequestException ex) {
            checkEligibleForRetry(bad, id, retries, ex, this.retriesChunks);
        }
    }

    private void pullKeyframe(int id) {
        try {
            game.pullKeyFrame(id);
            if (onKeyframePulled != null) { onKeyframePulled.accept(id); }
        } catch (RequestException ex) {
            if (ex.getErrorType() == RequestException.ErrorType.INTERNAL_SERVER_ERROR) {
                retriesKeyframes.put(id, 1);
            } else {
                throw ex;
            }
        }
    }


    private void retryKeyframe(Set<Integer> bad, Set<Integer> good, int id, int retries) {
        try {
            log.debug("[" + game.getGameId() + "] Reattempting to pull keyframe " + id + " (attempt " + retries + "/" + DEFAULT_MAX_RETRIES + ")");
            game.pullKeyFrame(id);
            good.add(id);   // Notify of success
        } catch (RequestException ex) {
            checkEligibleForRetry(bad, id, retries, ex, this.retriesKeyframes);
        }
    }

    private void checkEligibleForRetry(Set<Integer> ifBad, int id, int retries, RequestException ex, Map<Integer, Integer> retryCounts) {
        if (ex.getErrorType() == RequestException.ErrorType.INTERNAL_SERVER_ERROR) {
            if (retries < DEFAULT_MAX_RETRIES) {
                retryCounts.put(id, retries + 1);
            } else {
                ifBad.add(id); // Notify of attempts exceeded
            }
        } else {
            throw ex;
        }
    }

    private void pullAllAvailable() {
        ExecutorService service = Executors.newCachedThreadPool();

        ChunkInfo chunkInfo = game.getLastChunkInfo();

        for (int i = 1; i <= chunkInfo.getChunkId(); i++) {
            int ii = i;
            service.submit(() -> {
                try {
                    pullChunk(ii);
                } catch (RequestException ex) {
                    onError.accept(ex);
                }
            });
        }

        for (int i = 1; i <= chunkInfo.getKeyFrameId(); i++) {
            int ii = i;
            service.submit(() -> {
                try {
                    pullKeyframe(ii);
                } catch (Exception ex) {
                    onError.accept(ex);
                }
            });
        }

        service.shutdown();
    }
}

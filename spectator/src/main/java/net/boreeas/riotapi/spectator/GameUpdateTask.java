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

    private static final int DEFAULT_MAX_RETRIES = 3;

    private Consumer<Exception> onError;
    private InProgressGame game;

    @Setter private ScheduledFuture self;

    private boolean firstRun = true;
    private Map<Integer, Integer> retries = new HashMap<>();

    public GameUpdateTask(InProgressGame game) {
        this(game, err -> {});
    }

    public GameUpdateTask(InProgressGame game, Consumer<Exception> errorCallback) {
        this.game = game;
        this.onError = errorCallback;
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
        } else if (chunkInfo.getChunkId() > game.getLastAvailableChunk()) {
            pullAllSinceLast(chunkInfo.getChunkId());
        }

        if (chunkInfo.getKeyFrameId() != game.getLastAvailableKeyFrame()) {
            // Since we update every chunk, and keyframes don't appear as often as chunks,
            // assume at most one new keyframe
            try {
                game.pullKeyFrame(chunkInfo.getKeyFrameId());
            } catch (Exception ex) {
                onError.accept(ex);
            }
        }

        if (chunkInfo.getEndGameChunkId() != 0) {
            log.debug("[" + game.getGameId() + "] End of game reached at chunk " + chunkInfo.getEndGameChunkId());
            game.markEndReached();
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

    private void pullAllSinceLast(int maxId) {
        log.debug("[" + game.getGameId() + "] " + (maxId - game.getLastAvailableChunk()) + " new chunks (" + (game.getLastAvailableChunk() + 1) + " to " + maxId + ")");

        for (int id = game.getLastAvailableChunk() + 1; id <= maxId; id++) {
            try {
                game.pullChunk(id);
            } catch (RequestException ex) {
                if (ex.getErrorType() == RequestException.ErrorType.INTERNAL_SERVER_ERROR) {
                    retries.put(id, 1);
                } else {
                    throw ex;
                }
            }
        }

        Set<Integer> removeMarkers = new HashSet<>();
        for (Map.Entry<Integer, Integer> retryData: retries.entrySet()) {
            retryChunk(removeMarkers, retryData.getKey(), retryData.getValue());
        }

        removeMarkers.forEach(retries::remove);
    }

    private void retryChunk(Set<Integer> remove, int id, int retries) {
        try {
            game.pullChunk(id);
        } catch (RequestException ex) {
            if (ex.getErrorType() == RequestException.ErrorType.INTERNAL_SERVER_ERROR) {
                if (retries < DEFAULT_MAX_RETRIES) {
                    this.retries.put(id, retries + 1);
                } else {
                    remove.add(id);
                }
            } else {
                throw ex;
            }
        }
    }

    private void pullAllAvailable() {
        ExecutorService service = Executors.newCachedThreadPool();

        ChunkInfo chunkInfo = game.getLastChunkInfo();

        for (int i = 1; i <= chunkInfo.getChunkId(); i++) {
            int ii = i;
            service.submit(() -> {
                try {
                    game.pullChunk(ii);
                } catch (Exception ex) {
                    onError.accept(ex);
                }
            });
        }

        for (int i = 1; i <= chunkInfo.getKeyFrameId(); i++) {
            int ii = i;
            service.submit(() -> {
                try {
                    game.pullKeyFrame(ii);
                } catch (Exception ex) {
                    onError.accept(ex);
                }
            });
        }

        service.shutdown();
    }
}

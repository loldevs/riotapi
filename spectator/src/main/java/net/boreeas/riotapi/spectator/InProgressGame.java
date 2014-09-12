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
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.spectator.rest.ChunkInfo;
import net.boreeas.riotapi.spectator.rest.GameMetaData;

import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * Created on 4/28/2014.
 */
@Log4j
public class InProgressGame implements SpectatedGame {

    private SpectatorApiHandler handler;
    private Shard platform;
    private long gameId;

    private String b64key;
    @Getter private GameEncryptionData gameEncryptionData;
    private MappedDataCache<Integer, Chunk> chunks = new MappedDataCache<>();
    private MappedDataCache<Integer, KeyFrame> keyframes = new MappedDataCache<>();
    private GameMetaData cachedMetaData;
    private CountDownLatch endOfGame = new CountDownLatch(1);

    @Getter private int firstAvailableChunk;
    @Getter private int lastAvailableChunk;
    @Getter private int firstAvailableKeyFrame;
    @Getter private int lastAvailableKeyFrame;

    public InProgressGame(Shard region, Shard platform, long gameId, String initialEncryptionKey) {
        this(new SpectatorApiHandler(region), platform, gameId, initialEncryptionKey);
    }

    public InProgressGame(SpectatorApiHandler handler, Shard platform, long gameId, String initialEncryptionKey) {
        this.handler = handler;
        this.platform = platform;
        this.gameId = gameId;
        this.b64key = initialEncryptionKey;
        getMetaData(); // Init cache

        loadEncryptionKey(initialEncryptionKey);
    }

    @SneakyThrows
    private void loadEncryptionKey(String initial) {
        byte[] buf = Base64.getDecoder().decode(initial);

        String tmpKey = "" + gameId;
        GameEncryptionData tmpDecryptor = new GameEncryptionData(tmpKey.getBytes("UTF-8"));
        this.gameEncryptionData = new GameEncryptionData(tmpDecryptor.decrypt(buf));
    }

    public GameMetaData getMetaData() {
        // Force cache update
        cachedMetaData = handler.getGameMetaData(platform, gameId);
        cachedMetaData.setEncryptionKey(b64key);
        return cachedMetaData;
    }

    @Override
    public long getGameId() {
        return gameId;
    }

    @Override
    // TODO game length may not be set
    public long getGameLength() {
        return cachedMetaData.getGameLength();
    }

    @Override
    public int getKeyFrameCount() {
        return keyframes.size();
    }

    @Override
    public int getChunkCount() {
        return chunks.size();
    }

    @Override
    public int getEndStartupChunkId() {
        return cachedMetaData.getEndStartupChunkId();
    }

    @Override
    public int getGameStartChunkId() {
        return cachedMetaData.getStartGameChunkId();
    }

    @Override
    public long getKeyFrameInterval() {
        return cachedMetaData.getKeyFrameTimeInterval();
    }

    public long getChunkInterval() {
        return cachedMetaData.getChunkTimeInterval();
    }


    public boolean isChunkLoaded(int id) {
        return chunks.isReleased(id);
    }

    public boolean isKeyframeLoaded(int id) {
        return keyframes.isReleased(id);
    }

    @Override
    @SneakyThrows
    public Chunk getChunk(int i) {
        return chunks.get(i);
    }

    public Future<Chunk> getFutureChunk(int i) {
        return chunks.getFuture(i);
    }

    @Override
    @SneakyThrows
    public KeyFrame getKeyFrame(int i) {
        return keyframes.get(i);
    }

    public Future<KeyFrame> getFutureKeyFrame(int i) {
        return keyframes.getFuture(i);
    }

    public void pullChunk(int id) {
        log.debug("[" + gameId + "] Pulling chunk " + id);
        chunks.put(id, getAndDecodeChunk(id));

        if (id > lastAvailableChunk) lastAvailableChunk = id;
        if (id < firstAvailableChunk || firstAvailableChunk == 0) firstAvailableChunk = id;
    }

    public void pullKeyFrame(int id) {
        log.debug("[" + gameId + "] Pulling keyframe " + id);
        keyframes.put(id, getAndDecodeKeyFrame(id));

        if (id > lastAvailableKeyFrame) lastAvailableKeyFrame = id;
        if (id < firstAvailableKeyFrame || firstAvailableKeyFrame == 0) firstAvailableKeyFrame = id;
    }

    private Chunk getAndDecodeChunk(int chunkId) {
        byte[] raw = handler.getEncryptedChunk(platform, gameId, chunkId);
        return new Chunk(SpectatedGame.decompress(gameEncryptionData.decrypt(raw)));
    }

    private KeyFrame getAndDecodeKeyFrame(int keyframeId) {
        byte[] raw = handler.getEncryptedKeyframe(platform, gameId, keyframeId);
        return new KeyFrame(SpectatedGame.decompress(gameEncryptionData.decrypt(raw)));
    }

    public ChunkInfo getLastChunkInfo() {
        return handler.getLastChunkInfo(platform, gameId);
    }

    @SneakyThrows
    public void waitForEndOfGame() {
        endOfGame.await();
    }

    public void markEndReached() {
        endOfGame.countDown();
    }
}

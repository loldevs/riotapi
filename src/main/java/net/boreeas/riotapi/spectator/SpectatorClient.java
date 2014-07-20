/*
 * Copyright 2014 Malte Schütze
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

import net.boreeas.riotapi.Shard;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPInputStream;

/**
 * Created on 4/28/2014.
 */
public class SpectatorClient implements SpectatorSource {

    private SpectatorApiHandler handler;
    private Platform platform;
    private long gameId;
    private GameMetaData metaData;
    private String encryptionKey;
    private ChunkInfo lastChunkInfo;
    private Timer getChunkTimer = new Timer(true);
    private Cipher cipher;


    public SpectatorClient(Shard region, Platform platform, long gameId) {
        this.handler = new SpectatorApiHandler(region);
        this.platform = platform;
        this.gameId = gameId;

        initialize();
    }

    private void initialize() {
        metaData = handler.getGameMetaData(platform, gameId);
        lastChunkInfo = handler.getLastChunkInfo(platform, gameId);
    }

    private void scheduleReadNextChunk() {
        getChunkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ChunkInfo next = handler.getLastChunkInfo(platform, gameId);
                if (next.getChunkId() != lastChunkInfo.getChunkId()) {
                    lastChunkInfo = next;
                    scheduleReadNextChunk();

                    loadNextChunk();
                }
            }
        }, lastChunkInfo.getNextAvailableChunk());
    }

    private void loadNextChunk() {

    }

    private byte[] decrypt(byte[] data, byte[] key) throws GeneralSecurityException {
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "Blowfish"));
        return cipher.doFinal(data);
    }

    private byte[] decompress(byte[] data) throws IOException {
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(data))) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;

            do {
                read = in.read(buffer);
                if (read == buffer.length) {
                    bout.write(buffer);
                } else {
                    bout.write(buffer, 0, read);
                }
            } while (read == buffer.length);

            return bout.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Failure during decompression", ex);
        }

    }


    @Override
    public long getGameId() {
        return metaData.getGameId();
    }

    @Override
    public long getGameLength() {
        return metaData.getGameLength();
    }

    @Override
    public int getKeyFrameCount() {
        return 0;
    }

    @Override
    public int getChunkCount() {
        return 0;
    }

    @Override
    public int getEndStartupChunkId() {
        return 0;
    }

    @Override
    public int getGameStartChunkId() {
        return 0;
    }

    @Override
    public long getKeyFrameInterval() {
        return 0;
    }

    @Override
    public GameMetaData getMetaData() {
        return null;
    }

    @Override
    public byte[] getChunk(int i) {
        return new byte[0];
    }

    @Override
    public byte[] getKeyFrame(int i) {
        return new byte[0];
    }
}

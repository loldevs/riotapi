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

import net.boreeas.riotapi.spectator.rest.GameMetaData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created on 5/1/2014.
 */
public interface SpectatedGame {

    /**
     * The id of the spectated game.
     * @return The game id.
     */
    long getGameId();

    /**
     * The length of the game in milliseconds.
     * @return The length of the game.
     */
    long getGameLength();

    /**
     * @return The number of keyframes in this game.
     */
    int getKeyFrameCount();

    /**
     * @return The number of chunks in this game.
     */
    int getChunkCount();

    /**
     * @return The id of the last chunk representing the loading screen.
     */
    int getEndStartupChunkId();

    /**
     * @return The id of the first chunk representing the actual game.
     */
    int getGameStartChunkId();

    /**
     * @return The time between key frames in milliseconds.
     */
    long getKeyFrameInterval();

    /**
     * @return Game meta data.
     */
    GameMetaData getMetaData();

    /**
     * @return Information for decrypting the data.
     */
    GameEncryptionData getGameEncryptionData();

    /**
     * Retrieve the specified chunk.
     * @param i The id of the chunk.
     * @return The specified chunk.
     */
    Chunk getChunk(int i);

    /**
     * Retrieve the specified key frame.
     * @param i The id of the frame.
     * @return The frame.
     */
    KeyFrame getKeyFrame(int i);


    /**
     * Opens the target file as spectator game. Blocks until all chunks and keyframes are decrypted, to a maximum
     * of 5 seconds.
     * @param file The name of the file to open.
     * @return The saved game.
     * @throws IOException If an error occurred while reading or decompressing.
     * @throws GeneralSecurityException If an error occurred while decrypting.
     */
    public static SpectatedGame openFile(String file) throws IOException, GeneralSecurityException {
        return openFile(new File(file));
    }


    /**
     * Opens the target file as spectator game. Blocks until all chunks and keyframes are decrypted, to a maximum
     * of 5 seconds.
     * @param file The file to open.
     * @return The saved game.
     * @throws IOException If an error occurred while reading or decompressing.
     * @throws GeneralSecurityException If an error occurred while decrypting.
     */
    public static SpectatedGame openFile(File file) throws IOException, GeneralSecurityException {
        return new RoflFile(file, 5, TimeUnit.SECONDS);
    }


    /**
     * Decompress the data via gzip.
     * @param data Gzip-compressed data.
     * @return Decompressed data.
     */
    public static byte[] decompress(byte[] data) {

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(data))) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];

            int read;
            while ((read = in.read(buffer)) != -1) {
                bout.write(buffer, 0, read);
            }

            return bout.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Failure during decompression", ex);
        }
    }

    /**
     * Compress the data via gzip.
     * @param data Gzip-compressed data.
     * @return Compressed data
     */
    public static byte[] compress(byte[] data) {

        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(bout)) {

            gzip.write(data);
            gzip.flush();
            return bout.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Failure during decompression", ex);
        }
    }
}

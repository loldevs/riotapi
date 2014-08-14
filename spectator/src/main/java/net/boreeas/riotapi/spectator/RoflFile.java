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

import com.google.gson.Gson;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Synchronized;
import net.boreeas.riotapi.spectator.rest.GameMetaData;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created on 4/29/2014.
 */
public class RoflFile implements SpectatedGame {

    private static final int SIGNATURE_POS              = 6;
    private static final int HEADER_LEN_POS             = SIGNATURE_POS         + 256;
    private static final int FILE_LEN_POS               = HEADER_LEN_POS        + 2;
    private static final int META_DATA_OFFSET_POS       = FILE_LEN_POS          + 4;
    private static final int META_DATA_LEN_POS          = META_DATA_OFFSET_POS  + 4;
    private static final int PAYLOAD_HEADER_OFFSET_POS  = META_DATA_LEN_POS     + 4;
    private static final int PAYLOAD_HEADER_LEN_POS     = PAYLOAD_HEADER_OFFSET_POS + 4;
    private static final int PAYLOAD_OFFSET_POS         = PAYLOAD_HEADER_LEN_POS + 4;


    @Getter private GameEncryptionData gameEncryptionData;

    private byte[] data;
    private ByteBuffer buffer;
    private GameMetaData metaData;
    private List<Chunk> chunks = new ArrayList<>();
    private List<KeyFrame> keyframes = new ArrayList<>();

    @SneakyThrows(value = {TimeoutException.class, InterruptedException.class})
    public RoflFile(File f, long timeout, TimeUnit timeUnit) throws IOException, GeneralSecurityException {

        RandomAccessFile file = new RandomAccessFile(f, "r");
        long length = file.length();
        if (length > Integer.MAX_VALUE) throw new RuntimeException("File too large");

        data = new byte[(int) length];
        file.readFully(data);
        buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Assert we are actually reading a ROFL file
        assertHeader("RIOT\0\0".getBytes());

        loadMetadata();
        loadEncryptionKey();


        ExecutorService pool = Executors.newCachedThreadPool();

        // Load and decrypt chunks
        for (int i = 0; i < getChunkCount(); i++) {
            int ii = i; // Effectively final for passing to concurrent task
            pool.execute(() -> {
                ChunkOrKeyFrameHeader header = getChunkHeader(ii);
                chunks.add(new Chunk(decryptPayloadEntry(header)));
            });
        }

        // Load and decrypt keyframes
        for (int i = 0; i < getKeyFrameCount(); i++) {
            int ii = i; // Effectively final for passing to concurrent task
            pool.execute(() -> {
                ChunkOrKeyFrameHeader header = getKeyFrameHeader(ii);
                keyframes.add(new KeyFrame(decryptPayloadEntry(header)));
            });
        }

        pool.shutdown();
        if (!pool.awaitTermination(timeout, timeUnit)) {
            throw new TimeoutException("Chunk and keyframe encryption took more than " + timeout + " " + timeUnit);
        }
    }

    private void loadEncryptionKey() throws UnsupportedEncodingException, GeneralSecurityException {
        /**
         * Decryption process:
         * - Decode encryption key using base64         => buf
         * - Decrypt buf using gameId as key            => encryptionKey
         */
        byte[] encryptionKeyBuf = new byte[getEncryptionKeyLength()];
        buffer.position(getPayloadHeaderOffset() + 34);
        buffer.get(encryptionKeyBuf);

        byte[] encryptionKey = DatatypeConverter.parseBase64Binary(new String(encryptionKeyBuf, "UTF-8"));
        encryptionKey = new GameEncryptionData(("" + metaData.getGameId()).getBytes()).decrypt(encryptionKey);
        this.gameEncryptionData = new GameEncryptionData(encryptionKey);
    }

    private void loadMetadata() throws UnsupportedEncodingException {
        byte[] metaDataBuf = new byte[getMetaDataLength()];
        buffer.position(getMetaDataOffset());
        buffer.get(metaDataBuf);
        String json = new String(metaDataBuf, "UTF-8");
        metaData = new Gson().fromJson(json, GameMetaData.class);
    }

    @SneakyThrows
    @Synchronized
    private byte[] decryptPayloadEntry(ChunkOrKeyFrameHeader header) {

        byte[] keyFrame = new byte[(int) header.getLength()];
        buffer.position(getPayloadEntryOffset() + header.getOffset());
        buffer.get(keyFrame);

        return SpectatedGame.decompress(gameEncryptionData.decrypt(keyFrame));
    }

    // <editor-fold>
    public byte[] getSignature() {
        byte[] sigBuf = new byte[256];
        buffer.position(SIGNATURE_POS);
        buffer.get(sigBuf);
        return sigBuf;
    }

    public int getHeaderLength() {
        buffer.position(HEADER_LEN_POS);
        return getUshort();
    }

    public long getFileLength() {
        // Ex-uint
        return buffer.getInt(FILE_LEN_POS);
    }

    public int getMetaDataOffset() {
        // Ex-uint
        return buffer.getInt(META_DATA_OFFSET_POS);
    }

    public int getMetaDataLength() {
        // Ex-uint
        return buffer.getInt(META_DATA_LEN_POS);
    }

    public int getPayloadHeaderOffset() {
        // Ex-uint
        return buffer.getInt(PAYLOAD_HEADER_OFFSET_POS);
    }

    public int getPayloadHeaderLength() {
        // Ex-uint
        return buffer.getInt(PAYLOAD_HEADER_LEN_POS);
    }

    public int getPayloadOffset() {
        // Ex-uint
        return buffer.getInt(PAYLOAD_OFFSET_POS);
    }

    @Override
    public long getGameId() {
        return buffer.getLong(getPayloadHeaderOffset());
    }

    @Override
    public long getGameLength() {
        buffer.position(getPayloadHeaderOffset() + 8);
        return getUint();
    }

    @Override
    public int getKeyFrameCount() {
        return buffer.getInt(getPayloadHeaderOffset() + 12);
    }

    @Override
    public int getChunkCount() {
        return buffer.getInt(getPayloadHeaderOffset() + 16);
    }

    @Override
    public int getEndStartupChunkId() {
        return buffer.getInt(getPayloadHeaderOffset() + 20);
    }

    @Override
    public int getGameStartChunkId() {
        return buffer.getInt(getPayloadHeaderOffset() + 24);
    }

    @Override
    public long getKeyFrameInterval() {
        buffer.position(getPayloadHeaderOffset() + 28);
        return getUint();
    }

    public int getEncryptionKeyLength() {
        buffer.position(getPayloadHeaderOffset() + 32);
        return getUshort();
    }

    @Override
    public GameMetaData getMetaData() {
        return metaData;
    }

    public ChunkOrKeyFrameHeader getChunkHeader(int id) {
        if (id >= getChunkCount()) {
            throw new IndexOutOfBoundsException("Chunk id (" + id + ") exceeded chunk count (" + getChunkCount() + ")");
        }

        int position = getPayloadOffset() + (id * 17);

        return readChunkOrKeyframeHeader(position);
    }

    public ChunkOrKeyFrameHeader getKeyFrameHeader(int id) {
        if (id >= getKeyFrameCount()) {
            throw new IndexOutOfBoundsException("Keyframe id (" + id + ") exceeded keyframe count (" + getChunkCount() + ")");
        }

        int position = getPayloadOffset() + (getChunkCount() * 17) + (id * 17);

        return readChunkOrKeyframeHeader(position);
    }

    private ChunkOrKeyFrameHeader readChunkOrKeyframeHeader(int position) {
        int cid = buffer.getInt(position);
        int type = buffer.get(position + 4);
        long length = buffer.getInt(position + 5);
        int nextCid = buffer.getInt(position + 9);
        int offset = buffer.getInt(position + 13);

        return new ChunkOrKeyFrameHeader(cid, type, length, nextCid, offset);
    }

    @Override
    public Chunk getChunk(int i) {
        return chunks.get(i);
    }

    @Override
    public KeyFrame getKeyFrame(int i) {
        return keyframes.get(i);
    }
    // </editor-fold>



    private int getPayloadEntryOffset() {
        return getPayloadOffset() + (getChunkCount() * 17) + (getKeyFrameCount() * 17);
    }

    private int getUshort() {

        return ((int) buffer.getShort()) & 0xFFFF;
    }

    private long getUint() {
        return ((long) buffer.getInt()) & 0xFFFFFFFFL;
    }


    private void assertHeader(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (data[i] != bytes[i]) {
                throw new IllegalStateException("Buffer assertion failed at offset " + (0 + i));
            }
        }
    }
}

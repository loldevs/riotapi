/*
 * Copyright 2014 Malte Schütze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi.rest.spectator;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created on 4/29/2014.
 */
public class RoflFile implements SpectatorSource {

    private static final int SIGNATURE_POS              = 6;
    private static final int HEADER_LEN_POS             = SIGNATURE_POS         + 256;
    private static final int FILE_LEN_POS               = HEADER_LEN_POS        + 2;
    private static final int META_DATA_OFFSET_POS       = FILE_LEN_POS          + 4;
    private static final int META_DATA_LEN_POS          = META_DATA_OFFSET_POS  + 4;
    private static final int PAYLOAD_HEADER_OFFSET_POS  = META_DATA_LEN_POS     + 4;
    private static final int PAYLOAD_HEADER_LEN_POS     = PAYLOAD_HEADER_OFFSET_POS + 4;
    private static final int PAYLOAD_OFFSET_POS         = PAYLOAD_HEADER_LEN_POS + 4;

    private Cipher cipher;

    private byte[] data;
    private ByteBuffer buffer;
    private GameMetaData metaData;
    private byte[] encryptionKey;
    private List<byte[]> chunks = new ArrayList<>();
    private List<byte[]> keyframes = new ArrayList<>();

    public RoflFile(File f) throws IOException, GeneralSecurityException {
        cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");

        RandomAccessFile file = new RandomAccessFile(f, "r");
        long length = file.length();
        if (length > Integer.MAX_VALUE) throw new RuntimeException("File too large");

        data = new byte[(int) length];
        file.readFully(data);
        buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Assert we are actually reading a ROFL file
        assertBytes("RIOT\0\0".getBytes(), 0);

        // Get metadata
        byte[] metaDataBuf = new byte[(int) getMetaDataLength()];
        buffer.position(getMetaDataOffset());
        buffer.get(metaDataBuf);
        String json = new String(metaDataBuf, "UTF-8");
        metaData = new Gson().fromJson(new String(metaDataBuf, "UTF-8"), GameMetaData.class);

        // Get encryption key
        byte[] encryptionKeyBuf = new byte[getEncryptionKeyLength()];
        buffer.position(getPayloadHeaderOffset() + 34);
        buffer.get(encryptionKeyBuf);
        encryptionKey = DatatypeConverter.parseBase64Binary(new String(encryptionKeyBuf, "UTF-8"));
        encryptionKey = decrypt(encryptionKey, ("" + metaData.getGameId()).getBytes());

        // Load and decrypt chunks
        for (int i = 0; i < getChunkCount(); i++) {
            ChunkOrKeyFrameHeader header = getChunkHeader(i);
            chunks.add(decryptPayloadEntry(header));
        }

        // Load and decrypt keyframes
        for (int i = 0; i < getKeyFrameCount(); i++) {
            ChunkOrKeyFrameHeader header = getKeyFrameHeader(i);
            keyframes.add(decryptPayloadEntry(header));
        }
    }

    private byte[] decryptPayloadEntry(ChunkOrKeyFrameHeader header) throws GeneralSecurityException, IOException {

        byte[] keyFrame = new byte[(int) header.getLength()];
        buffer.position(getPayloadEntryOffset() + header.getOffset());
        buffer.get(keyFrame);

        return decompress(decrypt(keyFrame, encryptionKey));
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

    public byte[] getEncryptionKey() {
        return encryptionKey;
    }

    public ChunkOrKeyFrameHeader getChunkHeader(int id) {
        if (id >= getChunkCount()) {
            throw new IndexOutOfBoundsException("Chunk id (" + id + ") exceeded chunk count (" + getChunkCount() + ")");
        }

        buffer.position(getPayloadOffset() + (id * 17));
        int cid = buffer.getInt();
        int type = buffer.get();
        long length = getUint();
        int nextCid = buffer.getInt();
        int offset = buffer.getInt();

        return new ChunkOrKeyFrameHeader(cid, type, length, nextCid, offset);
    }

    public ChunkOrKeyFrameHeader getKeyFrameHeader(int id) {
        if (id >= getKeyFrameCount()) {
            throw new IndexOutOfBoundsException("Keyframe id (" + id + ") exceeded keyframe count (" + getChunkCount() + ")");
        }

        buffer.position(getPayloadOffset() + (getChunkCount() * 17) + (id * 17));
        int cid = buffer.getInt();
        int type = buffer.get();
        long length = getUint();
        int nextCid = buffer.getInt();
        int offset = buffer.getInt();

        return new ChunkOrKeyFrameHeader(cid, type, length, nextCid, offset);
    }

    @Override
    public byte[] getChunk(int i) {
        return chunks.get(i);
    }

    @Override
    public byte[] getKeyFrame(int i) {
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


    private void assertBytes(byte[] bytes, int offset) {
        for (int i = 0; i < bytes.length; i++) {
            if (data[offset + i] != bytes[i]) {
                throw new IllegalStateException("Buffer assertion failed at offset " + (offset + i));
            }
        }
    }

}

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
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

/**
 * Created on 4/29/2014.
 */
public class RoflFile {

    private Cipher cipher;

    private byte[] data;
    private ByteBuffer buffer;
    private GameMetaData metaData;
    private byte[] encryptionKey;

    public RoflFile(File f) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");;

        RandomAccessFile file = new RandomAccessFile(f, "r");
        long length = file.length();
        if (length > Integer.MAX_VALUE) throw new RuntimeException("File too large");

        data = new byte[(int) length];
        file.readFully(data);
        buffer = ByteBuffer.wrap(data);

        assertString("RIOT\0\0", 0);

        byte[] metaDataBuf = new byte[(int) getMetaDataLength()];
        buffer.get(metaDataBuf, (int) getMetaDataOffset(), metaDataBuf.length);
        String json = new String(metaDataBuf, "UTF-8");
        System.out.println(new JsonParser().parse(json));
        metaData = new Gson().fromJson(new String(metaDataBuf, "UTF-8"), GameMetaData.class);

        byte[] encryptionKeyBuf = new byte[getEncryptionKeyLength()];
        buffer.get(encryptionKeyBuf, (int) (getPayloadHeaderOffset() + 34), encryptionKeyBuf.length);
        encryptionKey = DatatypeConverter.parseBase64Binary(new String(encryptionKeyBuf, "UTF-8"));
    }

    public byte[] getSignature() {
        byte[] sigBuf = new byte[256];
        buffer.position(6);
        buffer.get(sigBuf);
        return sigBuf;
    }

    public int getHeaderLength() {
        buffer.position(262);
        return getUshort();
    }

    public long getFileLength() {
        buffer.position(264);
        return getUint();
    }

    public long getMetaDataOffset() {
        buffer.position(268);
        return getUint();
    }

    public long getMetaDataLength() {
        buffer.position(272);
        return getUint();
    }

    public long getPayloadHeaderOffset() {
        buffer.position(276);
        return getUint();
    }

    public long getPayloadHeaderLength() {
        buffer.position(280);
        return getUint();
    }

    public long getPayloadOffset() {
        buffer.position(284);
        return getUint();
    }

    public long getGameId() {
        return buffer.getLong((int) getPayloadHeaderOffset());
    }

    public long getGameLength() {
        buffer.position((int) (getPayloadHeaderOffset() + 8));
        return getUint();
    }

    public int getKeyFrameCount() {
        return buffer.getInt((int) (getPayloadHeaderOffset() + 12));
    }

    public int getChunkCount() {
        return buffer.getInt((int) (getPayloadHeaderOffset() + 16));
    }

    public int getEndStartupChunkId() {
        return buffer.getInt((int) (getPayloadHeaderOffset() + 20));
    }

    public int getGameStartChunkId() {
        return buffer.getInt((int) (getPayloadHeaderOffset() + 24));
    }

    public long getKeyFrameInterval() {
        buffer.position((int) (getPayloadHeaderOffset() + 28));
        return getUint();
    }

    public int getEncryptionKeyLength() {
        buffer.position((int) (getPayloadHeaderOffset() + 32));
    }

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

        buffer.position((int) (getPayloadOffset() + (id * 17)));
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

        buffer.position((int) (getPayloadOffset() + (getChunkCount() * 17) + (id * 17)));
        int cid = buffer.getInt();
        int type = buffer.get();
        long length = getUint();
        int nextCid = buffer.getInt();
        int offset = buffer.getInt();

        return new ChunkOrKeyFrameHeader(cid, type, length, nextCid, offset);
    }

    private byte[] decrypt(byte[] data, String key) throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher.init(Cipher.DECRYPT_MODE, );
    }




    private int getUshort() {
        return (buffer.get() << 8) | buffer.get();
    }

    private long getUint() {
        return (buffer.get() << 24) | (buffer.get() << 16) | (buffer.get() << 8) | buffer.get();
    }

    private void assertString(String s, int offset) {
        for (int i = 0; i < s.length(); i++) {
            if (buffer.asCharBuffer().charAt(offset + i) != s.charAt(i)) {
                throw new IllegalStateException("String assertion failed at offset " + (offset + i));
            }
        }
    }

}

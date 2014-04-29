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

package net.boreeas.riotapi.rtmp.packets;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.boreeas.riotapi.Util;
import net.boreeas.riotapi.rtmp.amf.AmfOutputStream;
import net.boreeas.riotapi.rtmp.amf.TypedObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created on 4/17/2014.
 */
public class RtmpInvoke extends RtmpPacket {

    public static final byte TYPE_AMF0 = 0x14;
    public static final byte TYPE_AMF3 = 0x11;

    @Getter @Setter private String command;
    @Getter @Setter private boolean useAmf3;
    private Object[] values;
    @Getter @Setter private int transactionId;

    private byte[] data;

    public RtmpInvoke(String command, int transactionId, boolean useAmf3, Object... values) {
        super(FMT_FULL_HEADER);
        this.command = command;
        this.useAmf3 = useAmf3;
        this.values = values;
        this.transactionId = transactionId;
    }

    private byte[] getData() {
        if (data == null) {
            data = writeObjToBuffer();
        }
        return data;
    }

    @SneakyThrows(IOException.class) // IOException shouldn't happen when writing to byte buffer
    private byte[] writeObjToBuffer() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AmfOutputStream amfOut = new AmfOutputStream(out);

        amfOut.writeAMF0String(command);
        amfOut.writeAMF0Number(transactionId);

        for (Object obj: values) {
            if (useAmf3) {
                if (obj instanceof TypedObject) {
                    amfOut.encodeAmf3ToAmf0((TypedObject) obj);
                } else {
                    amfOut.encodeAmf3ToAmf0(TypedObject.fromObject(obj));
                }
            } else {
                if (obj instanceof TypedObject) {
                    amfOut.encodeAmf0Object((TypedObject) obj);
                } else {
                    amfOut.encodeAmf0Object(TypedObject.fromObject(obj));
                }
            }
        }

        return out.toByteArray();
    }

    @Override
    public byte getType() {
        return useAmf3 ? TYPE_AMF3 : TYPE_AMF0;
    }

    @Override
    public int getLength() {
        return getData().length;
    }

    @Override
    public void writeBody(OutputStream out) throws IOException {

        System.out.println("Invoke " + command + " body: ");
        for (String line: Util.hexdump(getData())) {
            System.out.println(line);
        }

        out.write(getData(), 0, getChunkSize());
        for (int i = 1; i * getChunkSize() < getLength(); i++) {
            out.write(FMT_NO_HEADER << 6);  // Type 3 header - extend packet
            out.write(getData(), i * getChunkSize(), Math.min(getChunkSize(), getData().length - i * getChunkSize()));
        }

    }
}

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
import net.boreeas.riotapi.rtmp.RangeException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created on 4/17/2014.
 */
@Setter
@Getter
public abstract class RtmpPacket {

    public static final int FMT_FULL_HEADER = 0;
    public static final int FMT_NO_MSG_STREAM = 1;
    public static final int FMT_TIMESTAMP_ONLY = 2;
    public static final int FMT_NO_HEADER = 3;

    private static final Random rand = new Random();

    private int fmt;
    private int chunkStreamId;
    private int messageStreamId;
    private int timestamp;
    private int chunkSize = 128;

    public RtmpPacket(int fmt) {
        if (fmt < 0 || fmt > 3) {
            throw new RangeException(fmt);
        }

        this.fmt = fmt;
    }

    public void write(OutputStream out) throws IOException {

        if (chunkStreamId <= 64) {
            out.write((fmt << 6) | chunkStreamId);
        } else if (chunkStreamId <= 319) {
            out.write((fmt << 6));
            out.write(chunkStreamId - 64);
        } else {
            out.write((fmt << 6) | 1);
            out.write(chunkStreamId - 64);
            out.write((chunkStreamId - 64) >> 8);
        }


        if (fmt == 0) { // Type 0 - full header
            writeTimestamp(out);
            writeLength(out);
            writeType(out);
            writeMessageStreamId(out);
        } else if (fmt == 1) { // Type 1 - header without message stream id
            writeTimestamp(out);
            writeLength(out);
            writeType(out);
        } else if (fmt == 2) { // Type 2 - just timestamp
            writeTimestamp(out);
        } // Type 3 - nothing

        writeBody(out);
    }

    private void writeTimestamp(OutputStream out) throws IOException {
        if (timestamp > 0xFFFFFF) {
            out.write(0xFF);
            out.write(0xFF);
            out.write(0xFF);
        } else {
            out.write(timestamp >> 16);
            out.write(timestamp >> 8);
            out.write(timestamp);
        }
    }

    private void writeLength(OutputStream out) throws IOException {
        int length = getLength();
        out.write(length >> 16);
        out.write(length >> 8);
        out.write(length);
    }

    private void writeType(OutputStream out) throws IOException {
        out.write(getType());
    }

    private void writeMessageStreamId(OutputStream out) throws IOException {
        out.write(messageStreamId);
        out.write(messageStreamId >> 8);
        out.write(messageStreamId >> 16);
        out.write(messageStreamId >> 24);
    }

    public abstract byte getType();

    public abstract int getLength();

    public abstract void writeBody(OutputStream out) throws IOException;
}

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

package net.boreeas.riotapi.rtmp.p2;

import net.boreeas.riotapi.rtmp.p2.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.p2.serialization.ObjectEncoding;

import java.io.IOException;

/**
 * Created on 5/18/2014.
 */
public class RtmpPacketWriter {

    private AmfWriter writer;
    private ObjectEncoding encoding;

    public RtmpPacketWriter(AmfWriter writer, ObjectEncoding encoding) {
        this.writer = writer;
        this.encoding = encoding;
    }

    public void write(RtmpEvent body, int streamId, int msgStreamId) {
        RtmpHeader header = new RtmpHeader();
        RtmpPacket packet = new RtmpPacket(header, body);

        header.setStreamId(streamId);
        header.setMsgStreamId(msgStreamId);
        header.setTimestamp(body.getTimeStamp());
        header.setMessageType(body.getType());

        if (body.getHeader() != null) {
            header.setTimestampRelative(body.getHeader().isTimestampRelative());
        }

        write(packet);
    }

    private void write(RtmpPacket packet) {

    }

    public void writeAsynch(RtmpEvent evt, int streamId, int msgStreamId) {
        new Thread(() -> write(evt, streamId, msgStreamId)).start();
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {}
    }
}

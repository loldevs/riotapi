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

package net.boreeas.riotapi.rtmp;

import lombok.Getter;
import lombok.Setter;
import net.boreeas.riotapi.rtmp.packets.RtmpPacket;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created on 4/19/2014.
 */
public class RtmpPacketWriter {

    private OutputStream out;

    private int lastType;
    private int lastMessageStreamId;
    private int lastLength;
    @Getter @Setter private int chunkSize = 128;

    public RtmpPacketWriter(OutputStream out) {
        this.out = out;
    }

    public void writePacket(RtmpPacket packet) throws IOException {
        /*
        if (packet.getMessageStreamId() == lastMessageStreamId) {
            packet.setFmt(RtmpPacket.FMT_NO_MSG_STREAM);
        }
        if (packet.getType() == lastType && packet.getLength() == lastLength) {
            packet.setFmt(RtmpPacket.FMT_TIMESTAMP_ONLY);
        }

        lastType = packet.getType();
        lastMessageStreamId = packet.getMessageStreamId();
        lastLength = packet.getLength();
        */

        System.out.printf("Writing packet!\n\tfmt=%d\n\tchunkId=%d\n\ttimestamp=%d\n\ttype=%d\n\tmessageStreamId=%d\n\tbody len=%d%n",
                packet.getFmt(),
                packet.getChunkStreamId(),
                packet.getTimestamp(),
                packet.getType(),
                packet.getMessageStreamId(),
                packet.getLength());

        packet.setChunkSize(chunkSize);
        packet.write(out);
        out.flush();
    }
}

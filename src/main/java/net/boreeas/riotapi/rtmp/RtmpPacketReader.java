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

import lombok.Setter;
import net.boreeas.riotapi.Util;
import net.boreeas.riotapi.rtmp.amf.AmfInputStream;
import net.boreeas.riotapi.rtmp.packets.RtmpAbort;
import net.boreeas.riotapi.rtmp.packets.RtmpAckWindowSize;
import net.boreeas.riotapi.rtmp.packets.RtmpAcknowledgement;
import net.boreeas.riotapi.rtmp.packets.RtmpPacket;
import net.boreeas.riotapi.rtmp.packets.RtmpSetChunkSize;
import net.boreeas.riotapi.rtmp.packets.RtmpSetPeerBandwidth;

import java.io.IOException;

/**
 * Created on 4/18/2014.
 */
public class RtmpPacketReader {
    private final AmfInputStream in;

    private int lastTimestamp;
    private int lastLength;
    private byte lastType;
    private int lastMsgStreamId;
    @Setter private int chunkSize = 128;

    private int type3LengthConsumed;

    public RtmpPacketReader(AmfInputStream in) {
        this.in = in;
    }

    public RtmpPacket readNextPacket() throws IOException {
        int b = in.read();

        byte fmt = (byte) (b >> 6);
        int streamId = b & 0x3F;

        if (streamId == 0) {
            streamId = in.read() + 64;
        } else if (streamId == 1) {
            streamId = in.read() + (in.read() << 8) + 64;
        }

        int timestamp = lastTimestamp;
        int length = lastLength;
        byte type = lastType;
        int msgStreamId = lastMsgStreamId;

        if (fmt < 3) {
            timestamp = (in.read() << 16) | (in.read() << 8) | in.read();
            this.lastTimestamp = timestamp;
        }

        if (fmt < 2) {
            length = (in.read() << 16) | (in.read() << 8) | in.read();
            type = (byte) in.read();
            this.lastLength = length;
            this.lastType = type;
        }

        if (fmt < 1) {
            msgStreamId = (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
            this.lastMsgStreamId =  msgStreamId;
        }


        RtmpPacket packet;
        if (type == RtmpSetChunkSize.TYPE) {
            packet = RtmpSetChunkSize.fromStream(in);
        } else if (type == RtmpAbort.TYPE) {
            packet = RtmpAbort.fromStream(in);
        } else if (type == RtmpAcknowledgement.TYPE) {
            packet = RtmpAcknowledgement.fromStream(in);
        } else if (type == RtmpAckWindowSize.TYPE) {
            packet = RtmpAckWindowSize.fromStream(in);
        } else if (type == RtmpSetPeerBandwidth.TYPE) {
            packet = RtmpSetPeerBandwidth.fromStream(in);
        } else {
            byte[] data = new byte[length];
            in.read(data);
            throw new UnknownPacketException("Unknown packet type " + type, Util.hexdump(data));
        }

        packet.setFmt(fmt);
        packet.setChunkStreamId(streamId);
        packet.setTimestamp(timestamp);
        packet.setMessageStreamId(msgStreamId);

        return packet;
    }


}

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
import net.boreeas.riotapi.rtmp.amf.AmfInputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created on 4/19/2014.
 */
public class RtmpSetPeerBandwidth extends RtmpPacket {
    public static final byte TYPE = 6;

    public static final byte LIMIT_HARD = 0;
    public static final byte LIMIT_SOFT = 1;
    public static final byte LIMIT_DYNAMIC = 2;
    @Getter private int ackWindowSize;
    @Getter private byte limitType;

    public RtmpSetPeerBandwidth(int ackWindowSize, byte limitType) {
        super(FMT_FULL_HEADER);
        this.ackWindowSize = ackWindowSize;
        this.limitType = limitType;
    }

    @Override
    public byte getType() {
        return TYPE;
    }

    @Override
    public int getLength() {
        return 5;
    }

    @Override
    public void writeBody(OutputStream out) throws IOException {
        out.write(ackWindowSize >> 24);
        out.write(ackWindowSize >> 16);
        out.write(ackWindowSize >> 8);
        out.write(ackWindowSize);
        out.write(limitType);
    }

    public static RtmpSetPeerBandwidth fromStream(AmfInputStream in) throws IOException {
        RtmpSetPeerBandwidth res = new RtmpSetPeerBandwidth(0, (byte) 0);
        res.ackWindowSize = (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
        res.limitType = (byte) in.read();

        return res;
    }
}

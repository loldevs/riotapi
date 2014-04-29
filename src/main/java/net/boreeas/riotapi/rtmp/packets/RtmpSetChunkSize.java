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
public class RtmpSetChunkSize extends RtmpPacket {

    public static final byte TYPE = 1;

    @Getter private int chunksize;

    public RtmpSetChunkSize(int chunksize) {
        super(FMT_FULL_HEADER);
        this.chunksize = chunksize;
    }

    @Override
    public byte getType() {
        return TYPE;
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public void writeBody(OutputStream out) throws IOException {
        out.write(chunksize >> 24);
        out.write(chunksize >> 16);
        out.write(chunksize >> 8);
        out.write(chunksize);
    }

    public static RtmpSetChunkSize fromStream(AmfInputStream in) throws IOException {
        RtmpSetChunkSize res = new RtmpSetChunkSize(0);
        res.chunksize = (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
        res.chunksize &= 0xFFFFFF;

        return res;
    }
}

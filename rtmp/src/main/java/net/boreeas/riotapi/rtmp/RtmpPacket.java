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

package net.boreeas.riotapi.rtmp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created on 4/25/2014.
 */
@Getter
@ToString
public class RtmpPacket {
    @Setter private RtmpHeader header;
    @Setter private RtmpEvent body;
    private byte[] buffer;
    private int currentPos;
    private int length;

    public RtmpPacket(RtmpHeader header) {
        this.header = header;
        this.length = header.getPacketLength();
        this.buffer = new byte[length];
    }

    public RtmpPacket(RtmpHeader header, RtmpEvent body) {
        this(header);
        this.body = body;
    }

    public boolean isComplete() {
        return currentPos == length;
    }

    public void append(byte[] b) {
        System.arraycopy(b, 0, buffer, currentPos, b.length);
        currentPos += b.length;
    }
}

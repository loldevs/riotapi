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

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created on 4/23/2014.
 */
@Data
@AllArgsConstructor
public class RtmpHeader {
    private HeaderType messageType;

    private int packetLength;
    private int streamId;
    private int msgStreamId;
    private int timestamp;

    public int getStreamIdBytes() {
        if (streamId < 0 || streamId > 65599) {
            throw new RuntimeException("Illegal streamId");
        } else if (streamId <= 63) {
            return 1;
        } else if (streamId <= 319) {
            return 2;
        } else  /* streamId <= 65599) */ {
            return 3;
        }
    }

    public int formatTypeAndStreamId() {
        switch (getStreamIdBytes()) {
            case 1: return messageType.shiftId() | streamId;
            case 2: return (messageType.shiftId() << 8) | (streamId - 64);
            case 3: return (messageType.shiftId() << 16) | (streamId - 64);
            default: throw new IllegalStateException();
        }
    }

}

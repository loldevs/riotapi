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

package net.boreeas.riotapi.spectator.chunks;

/**
 * Block header flags
 *
 * @author Malte Schütze
 */
public class Flags {
    public static final int RELATIVE_TIME = 1 << 7;
    public static final int NO_BLOCKTYPE = 1 << 6;
    public static final int SHORT_BLK_PARAM = 1 << 5;
    public static final int SHORT_CONTENT_LENGTH = 1 << 4;

    private byte flags;

    public Flags(byte flags) {
        this.flags = flags;
    }

    public boolean hasFlag(int flag) {
        return (flags & flag) == flag;
    }

    public String toString() {
        return String.format("Flags(%2x/%8s)", flags & 0xff, Integer.toBinaryString(flags & 0xff));
    }
}

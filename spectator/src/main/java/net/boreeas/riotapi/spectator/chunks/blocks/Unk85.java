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

package net.boreeas.riotapi.spectator.chunks.blocks;

import lombok.Value;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.spectator.chunks.Block;
import net.boreeas.riotapi.spectator.chunks.BlockHeader;
import net.boreeas.riotapi.spectator.chunks.BlockType;
import net.boreeas.riotapi.spectator.chunks.IsBlock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Malte Schütze
 */
@Value
@Log4j
@IsBlock(BlockType.UNK_85)
public class Unk85 extends Block {
    private int counter;
    private float unk1;
    private float unk2;

    public Unk85(BlockHeader header, byte[] data) {
        super(header, data);

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        counter = buffer.get() & 0xff;
        unk1 = buffer.getFloat();
        unk2 = buffer.getFloat();

        if (unk1 != 0) {
            log.warn("Unk:0x85: Expexted unk1 = 0.0, but was " + unk1);
        }

        if (unk2 != -1) {
            log.warn("Unk:0x85: Expected unk2 = -1.0, but was " + unk2);
        }

        assertEndOfBuffer(buffer);
    }
}

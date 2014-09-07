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
@IsBlock(BlockType.START_DEATHTIMER)
public class StartDeathtimer extends Block {
    private long entityId;
    private float duration;

    public StartDeathtimer(BlockHeader header, byte[] data) {
        super(header, data);

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        this.entityId = header.getBlockParam();

        long unk1 = buffer.getInt() & 0xffffffffL;
        if (unk1 != 0) {
            log.warn("Expected unk1 = 0, but got " + unk1);
        }

        long unk2 = buffer.getInt() & 0xffffffffL;
        if (unk2 != 7) {
            log.warn("Expected unk2 = 7, but got " + unk2);
        }

        this.duration = buffer.getFloat();

        int unk3 =  buffer.getShort() & 0xffff;
        if (unk3 != 0x4c00) {
            log.warn("Expected unk3 = 0x4c00, but got " + unk3);
        }

        assertEndOfBuffer(buffer);
    }
}


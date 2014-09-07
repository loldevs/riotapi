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
import net.boreeas.riotapi.Util;
import net.boreeas.riotapi.spectator.chunks.Block;
import net.boreeas.riotapi.spectator.chunks.BlockHeader;
import net.boreeas.riotapi.spectator.chunks.BlockType;
import net.boreeas.riotapi.spectator.chunks.IsBlock;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * @author Malte Schütze
 */
@Value
@Log4j
@IsBlock(BlockType.CREATE_EFFECT)
public class CreateEffect extends Block {
    private short effectId;
    private String name;

    public CreateEffect(BlockHeader header, byte[] data) {
        super(header, data);


        ByteBuffer buffer = ByteBuffer.wrap(data);
        effectId = buffer.getShort();

        String name ="";
        try {
            name = readNullterminatedString(buffer);
        } catch (BufferUnderflowException ex) {
            log.debug("[CREATE_EFFECT] Edge case: buffer underflow from name");
            Util.hexdump(data).forEach(log::debug);
        }

        this.name = name;

        if (buffer.remaining() > 0) {
            log.warn("[CREATE_EFFECT] Edge case: extra bytes");
            Util.hexdump(data).forEach(log::debug);
        }
        assertEndOfBuffer(buffer);
    }
}

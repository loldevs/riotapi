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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.Util;

import java.io.ByteArrayOutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * A block of data in a keyframe or chunk
 * @author Malte Schütze
 */
@Log4j
@AllArgsConstructor
public class Block {
    @Getter private BlockHeader header;
    protected byte[] buffer;


    protected void assertEndOfBuffer(ByteBuffer buffer) {
        if (buffer.remaining() > 0) {
            log.warn("[" + BlockType.getById(header.getType()) + "] Expected end of buffer, but got " + buffer.remaining() + " remaining");

            byte[] b = new byte[buffer.remaining()];
            buffer.get(b);

            Util.hexdump(b).forEach(log::warn);
        }
    }

    @SneakyThrows
    protected String readNullterminatedString(ByteBuffer buffer) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            byte read;
            while ((read = buffer.get()) != 0x00) {
                bout.write(read);
            }

            return new String(bout.toByteArray(), "UTF-8");
        } catch (BufferUnderflowException ex) {
            log.warn("[" + BlockType.getById(header.getType()) + "] Hit end of buffer during cstr: " + new String(bout.toByteArray(), "UTF-8"));
            throw ex;
        }

    }
}

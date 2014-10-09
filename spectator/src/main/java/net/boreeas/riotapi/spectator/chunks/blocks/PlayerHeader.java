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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Malte Schütze
 */
@Value
@Log4j
@IsBlock(BlockType.PLAYER_HEADER)
public class PlayerHeader extends Block {
    private long entityId;
    private int playerNumber;
    private float unk1;
    private int unk2;
    private int unk3;
    private int unk4;
    private long skinId;
    private String name;
    private String champion;
    private int unk5;

    public PlayerHeader(BlockHeader header, byte[] data) {
        super(header, data);

        log.warn("[PLAYER_HEADER] Unknown format");
        Util.hexdump(data).forEach(log::warn);

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        this.entityId = buffer.getInt() & 0xffffffffL;
        this.playerNumber = buffer.get() & 0xff;
        this.unk1 = buffer.getFloat();
        this.unk2 = buffer.getShort() & 0xffff;
        this.unk3 = buffer.getShort() & 0xffff;
        this.unk4 = buffer.get() & 0xff;
        this.skinId = buffer.getInt() & 0xffffffffL;

        int preStringPos = buffer.position();
        this.name = readNullterminatedString(buffer);

        buffer.position(preStringPos + 0x80);

        this.champion = readNullterminatedString(buffer);

        if (buffer.position() + 0x40 < buffer.capacity()) {
            System.out.println("Position: " + preStringPos + " / " + (preStringPos + 0x80 + 0x40) + " - " + buffer.capacity());
            buffer.position(preStringPos + 0x80 + 0x40);


            this.unk5 = buffer.get() & 0xff;

            assertEndOfBuffer(buffer);
        } else {
            log.warn("[PLAYER_HEADER] Unexpectedly short length: " + (buffer.capacity() - buffer.position() - 1) + " champion name buffer");

            this.unk5 = buffer.get(buffer.capacity() - 1) & 0xff;
        }



        if (unk1 != 2) {
            log.warn("[PLAYER_HEADER] Expected unk1 = 2.0, but got " + unk1);
        }

        if (unk2 != 1) {
            log.warn("[PLAYER_HEADER] Expected unk2 = 1, but got " + unk2);
        }

        if (unk3 != 0) {
            log.warn("[PLAYER_HEADER] Expected unk3 = 0, but got " + unk3);
        }

        if (unk4 != 0xff) {
            log.warn("[PLAYER_HEADER] Expected unk4 = 0xff, but got " + Integer.toHexString(unk4));
        }

        if (unk5 != 0) {
            log.warn("[PLAYER_HEADER] Expected unk5 = 0, but got " + unk5);
        }
    }
}

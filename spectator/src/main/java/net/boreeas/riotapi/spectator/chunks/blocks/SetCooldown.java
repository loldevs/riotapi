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
@IsBlock(BlockType.SET_COOLDOWN)
public class SetCooldown extends Block {
    private int slot;
    private float maxCooldown;
    private float currentCooldown;

    public SetCooldown(BlockHeader header, byte[] data) {
        super(header, data);

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        slot = buffer.get() & 0xff;
        float maxCooldown = buffer.getFloat();
        float currentCooldown = buffer.getFloat();

        if (currentCooldown == -1) {
            currentCooldown = 0;
        }

        if (maxCooldown == 0) {
            maxCooldown = currentCooldown;
        }

        this.maxCooldown = maxCooldown;
        this.currentCooldown = currentCooldown;

        assertEndOfBuffer(buffer);
    }


    public enum Slot {
        SPELL_Q(0),
        SPELL_W(1),
        SPELL_E(2),
        SPELL_R(3),
        ITEM_1(4),
        ITEM_2(5),
        ITEM_3(6),
        ITEM_4(7),
        ITEM_5(8),
        ITEM_6(9),
        TRINKET(10);

        public final int id;

        private Slot(int id) {
            this.id = id;
        }

        public static Slot getById(int id) {
            for (Slot slot: values()) {
                if (slot.id == id) {
                    return slot;
                }
            }

            throw new IllegalArgumentException("Unknown Slot: " + id);
        }
    }
}

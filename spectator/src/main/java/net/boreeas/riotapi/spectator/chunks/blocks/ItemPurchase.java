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
@IsBlock(BlockType.ITEM_PURCHASE)
public class ItemPurchase extends Block {
    private long entityId;
    private long itemId;
    private int slotId;
    private int countOnSlot;

    public ItemPurchase(BlockHeader header, byte[] buffer) {
        super(header, buffer);

        entityId = header.getBlockOwner();
        ByteBuffer b = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
        itemId = b.getInt() & 0xffffffffL;
        slotId = b.get() & 0xff;
        countOnSlot = b.getShort() & 0xffff;

        byte unk1 = b.get();
        if (unk1 != 0x40) {
            log.warn("[ITEM_PURCHASE] Expected unk1 = 0x40, but was " + unk1);
        }

        assertEndOfBuffer(b);
    }
}

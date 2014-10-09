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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Malte Schütze
 */
@Value
@Log4j
@IsBlock(BlockType.ATTRIBUTE_GROUP)
public class AttributeGroup extends Block {
    private long timestamp;
    private Map<Long, AttributeUpdate> updates = new HashMap<>();

    public AttributeGroup(BlockHeader header, byte[] data) {
        super(header, data);

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        this.timestamp = buffer.getInt() & 0xffffffffL;

        int numUpdates = buffer.get() & 0xff;
        for (int i = 0; i < numUpdates; i++) {
            AttributeUpdate update = readUpdate(buffer);
            this.updates.put(update.entityId, update);
        }

        assertEndOfBuffer(buffer);
    }

    private AttributeUpdate readUpdate(ByteBuffer buffer) {
        byte mask = buffer.get();
        long entityId = buffer.getInt() & 0xffffffffL;

        Map<Attribute, Number> attributes = new EnumMap<>(Attribute.class);
        for (int i = 0; i < 8; i++) {
            if ((mask & (1 << i)) == 0) { continue; }

            int attributeMask = buffer.get();

            int groupSize = buffer.get() & 0xff;
            int startPos = buffer.position();

            for (int j = 0; j < 32; j++) {
                if ((attributeMask & (1 << j)) == 0) { continue; }

                Attribute attribute = Attribute.getByGroupAndId(i, j);
                if (attribute == null) {
                    log.warn("[ATTRIBUTE_GROUP] Unknown attributes (" + i + ", " + j + "), skipping all remaining attributes in group");
                    buffer.position(startPos + groupSize);
                    break;
                }

                attributes.put(attribute, attribute.read(buffer));
            }

            if (buffer.position() != startPos + groupSize) {
                int delta = startPos + groupSize - buffer.position();
                log.warn("[ATTRIBUTE_GROUP] Not enough bytes read in group " + i + " - " + delta + " remaining (attr mask was "  + Integer.toBinaryString(attributeMask) + ")");

                buffer.position(startPos + groupSize);
            }
        }

        return new AttributeUpdate(entityId, attributes);
    }

    @Value
    public static class AttributeUpdate {
        private long entityId;
        private Map<Attribute, Number> attributes;
    }

    public enum Attribute {
        CURRENT_GOLD(0, 0),
        TOTAL_GOLD(0, 1),

        BASE_ATTACK_DAMAGE(2, 5),
        BASE_ABILITY_POWER(2, 6),
        CRIT_CHANCE(2, 8),
        ARMOR(2, 9),
        MAGIC_RESISTANCE(2, 10),
        HP5(2, 11),
        MP5(2, 12),
        BASIC_ATTACK_RANGE(2, 13),
        FLAT_BONUS_ATTACK_DAMAGE(2, 14),
        PCT_BONUS_ATTACK_DAMAGE(2, 15),
        FLAT_BONUS_ABILITY_POWER(2, 16),
        PCT_ATTACK_SPEED(2, 19),
        COOLDOWN_REDUCTION(2, 22),
        FLAT_ARMOR_PENETRATION(2, 25),
        PCT_ARMOR_PENETRATION(2, 26),
        FLAT_MAGIC_PENETRATION(2, 27),
        PCT_MAGIC_PENETRATION(2, 28),
        PCT_LIFESTEAL(2, 29),
        PCT_SPELLVAMP(2, 30),
        TENACITY(2, 31),



        CURRENT_HEALTH(4, 0),
        CURRENT_MANA(4, 1),
        MAX_HEALTH(4, 2),
        MAX_MANA(4, 3),
        EXPERIENCE(4, 4),
        VISION_RANGE(4, 9),
        MOVEMENT_SPEED(4, 10),
        MODEL_SIZE(4, 11),
        LEVEL(4, 12);


        public final int group;
        public  final int attributeId;
        private static final Map<Integer, Map<Integer, Attribute>> lookup = new HashMap<>();


        private Attribute(int group, int attr) {
            this.group = group;
            this.attributeId = attr;

            AttrLookup.set(group, attr, this);
        }

        public Number read(ByteBuffer buffer) {
            switch (this) {
                case LEVEL:
                    return buffer.get();

                default:
                    return buffer.getFloat();
            }
        }

        public static Attribute getByGroupAndId(int group, int attr) {
            return AttrLookup.get(group, attr);
        }
    }

    private static class AttrLookup {
        private static final Map<Integer, Map<Integer, Attribute>> lookup = new HashMap<>();

        public static void set(int group, int attrId, Attribute value) {
            if (!lookup.containsKey(group)) {
                synchronized (lookup) {
                    if (!lookup.containsKey(group)) {
                        lookup.put(group, new HashMap<>());
                    }
                }
            }

            lookup.get(group).put(attrId, value);
        }

        public static Attribute get(int group, int attrId) {
            if (!lookup.containsKey(group)) {
                return null;
            }

            return lookup.get(group).get(attrId);
        }
    }
}

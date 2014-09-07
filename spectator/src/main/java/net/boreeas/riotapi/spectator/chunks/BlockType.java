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
 * @author Malte Schütze
 */
public enum BlockType {
    MINION_SPAWN(0x03),
    ABILITY_LEVEL(0x15),
    CREATE_EFFECT(0x17),
    EXPERIENCE_GAIN(0x22),
    PLAYER_HEADER(0x4c),
    START_DEATHTIMER(0x5e),
    MOVEMENT(0x61),
    START_SPAWN(0x62),
    NEUTRAL_CAMP_SPAWN(0x63),
    ITEM_PURCHASE(0x6f),
    UNK_85(0x85),
    TOWER_DATA(0x9d),
    GOLD_GAIN(0xe4),

    UNKNOWN(0x00);

    private final int id;

    private BlockType(int id) {
        this.id = id;
    }

    public static BlockType getById(int id) {
        for (BlockType type: BlockType.values()) {
            if (type.id == id) {
                return type;
            }
        }

        return UNKNOWN;
    }
}

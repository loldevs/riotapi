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

import net.boreeas.riotapi.spectator.chunks.blocks.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Malte Schütze
 */
public enum DefaultBlockFactory implements BlockFactory {
    INSTANCE;

    private final Map<Integer, Class<? extends Block>> blockClasses = new HashMap<>();


    public Block getBlock(BlockHeader header, byte[] buffer) {
        BlockType type = BlockType.getById(header.getType());
        switch (type) {
            case ABILITY_LEVEL:
                return new AbilityLevel(header, buffer);

            case EXPERIENCE_GAIN:
                return new ExperienceGain(header, buffer);

            case CREATE_EFFECT:
                return new CreateEffect(header, buffer);

            case GOLD_GAIN:
                return new GoldGain(header, buffer);

            case ITEM_PURCHASE:
                return new ItemPurchase(header, buffer);

            case MINION_SPAWN:
                return new MinionSpawn(header, buffer);

            case MOVEMENT:
                return new Movement(header, buffer);

            case NEUTRAL_CAMP_SPAWN:
                return new NeutralCampSpawn(header, buffer);

            case START_DEATHTIMER:
                return new StartDeathtimer(header, buffer);

            case TOWER_DATA:
                return new TowerData(header, buffer);

            case UNK_85:
                return new Unk85(header, buffer);

            case START_SPAWN:
                return new StartSpawn(header, buffer);

            case PLAYER_HEADER:
                return new PlayerHeader(header, buffer);

            case UNKNOWN:
            default:
                Logger.getLogger(DefaultBlockFactory.class).warn("Unknown block type 0x" + Integer.toHexString(header.getType()) + "/" + type);
                return new Block(header, buffer);
        }
    }
}

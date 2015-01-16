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

package net.boreeas.riotapi.constants;

import net.boreeas.riotapi.com.riotgames.platform.game.map.GameMap;

/**
 * Created on 4/12/2014.
 */
public enum Map {
    SR_SUMMER(1, "Summoner's Rift", GameMap.SUMMONERS_RIFT),
    SR_AUTUMN(2, "Summoner's Rift", GameMap.SUMMONERS_RIFT),
    PROVING_GROUNDS(3, "The Proving Grounds", GameMap.PROVING_GROUNDS),
    TWISTED_TREELINE_ORIG(4, "Twisted Treeline (Original)", GameMap.TWISTED_TREELINE_OLD),
    CRYSTAL_SCAR(8, "The Crystal Scar", GameMap.CRYSTAL_SCAR),
    TWISTED_TREELINE_CURR(10, "Twisted Treeline (Current)", GameMap.TWISTED_TREELINE),
    /**
     * summoners rift beta map
     * @deprecated Use {@link #SR_NEW} instead
     */
    @Deprecated
    DEBUG_MAP(11, "Summoner", null),
    SR_NEW(11, "Summoner's Rift (Updated)", GameMap.SUMMONERS_RIFT),
    HOWLING_ABYSS(12, "Howling Abyss", GameMap.HOWLING_ABYSS);


    public final int id;
    public final String name;
    public final GameMap gameMap;

    private Map(int id, String name, GameMap gameMap) {
        this.id = id;
        this.name = name;
        this.gameMap = gameMap;
    }

    public static Map getById(int id) {
        switch (id) {
            case 1: return SR_SUMMER;
            case 2: return SR_AUTUMN;
            case 3: return PROVING_GROUNDS;
            case 4: return TWISTED_TREELINE_ORIG;
            case 8: return CRYSTAL_SCAR;
            case 10: return TWISTED_TREELINE_CURR;
            case 12: return HOWLING_ABYSS;
            default: return null;
        }
    }
}

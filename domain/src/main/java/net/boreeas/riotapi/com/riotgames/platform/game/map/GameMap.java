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

package net.boreeas.riotapi.com.riotgames.platform.game.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.boreeas.riotapi.constants.Map;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

/**
 * Created on 7/19/2014.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"mapId"})
@Serialization(name = "com.riotgames.platform.game.map.GameMap")
public class GameMap {
    private String displayName;
    private String name;
    private int mapId;
    private int minCustomPlayers;
    private int totalPlayers;
    private String description;

    public Map getMapEnum() {
        return Map.getById(mapId);
    }


    public static final GameMap SUMMONERS_RIFT = new GameMap(
            "Summoner's Rift",
            "SummonersRift",
            1,
            1,
            10,
            "The oldest and most venerated Field of Justice is known as Summoner's Rift.  This battleground is known " +
            "for the constant conflicts fought between two opposing groups of Summoners.  Traverse down one of three " +
            "different paths in order to attack your enemy at their weakest point.  Work with your allies to siege " +
            "the enemy base and destroy their Headquarters!"
    );

    public static final GameMap CRYSTAL_SCAR = new GameMap(
            "The Crystal Scar",
            "CrystalScar",
            8,
            1,
            10,
            "The Crystal Scar was once known as the mining village of Kalamanda, until open war between Demacia and " +
            "Noxus broke out over control of its vast underground riches. Settle your disputes on this Field of " +
            "Justice by working with your allies to seize capture points and declare dominion over your enemies!"
    );

    public static final GameMap TWISTED_TREELINE = new GameMap(
            "The Twisted Treeline",
            "TwistedTreeline",
            10,
            1,
            6,
            "Deep in the Shadow Isles lies a ruined city shattered by magical disaster. Those who venture inside the " +
            "ruins and wander through the Twisted Treeline seldom return, but those who do tell tales of horrific " +
            "creatures and the vengeful dead."
    );

    public static final GameMap HOWLING_ABYSS = new GameMap(
            "Howling Abyss",
            "HowlingAbyss",
            12,
            1,
            10,
            "The Howling Abyss is a bottomless crevasse located in the coldest, cruelest, part of the Freljord. " +
            "Legends say that, long ago, a great battle took place here on the narrow bridge spanning this chasm. No " +
            "one remembers who fought here, or why, but it is said that if you listen carefully to the wind you can " +
            "still hear the cries of the vanquished tossed howling into the Abyss."
    );

    public static final GameMap PROVING_GROUNDS = new GameMap(
            "The Proving Grounds",
            "ProvingGrounds",
            3,
            1,
            10,
            "???"
    );

    public static final GameMap TWISTED_TREELINE_OLD = new GameMap(
            "The Twisted Treeline",
            "TwistedTreeline",
            4,
            1,
            6,
            "???"
    );

    public static final GameMap SUMMONERS_RIFT_NEW = new GameMap(
            "Summoner's Rift (Beta)",
            "SummonersRift",
            11,
            1,
            10,
            "The newest and most venerated Field of Justice is known as Summoner's Rift.  Traverse down one of three " +
            "different paths in order to attack your enemy at their weakest point.  Work with your allies to siege " +
            "the enemy base and destroy their Nexus!"
    );

    /**
     * Not accessible to clients
     */
    public static final GameMap DEBUG_AMP = new GameMap(
            "Debug Map",
            "DebugMap",
            0,
            0,
            0,
            "???"
    );
}

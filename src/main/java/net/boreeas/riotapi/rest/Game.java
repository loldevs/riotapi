/*
 * Copyright 2014 Malte Schütze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    private int championId;
    private long createDate;
    private List<Player> fellowPlayers;
    private long gameId;
    private String gameMode;
    private String gameType;
    private boolean invalid;
    private int ipEarned;
    private int level;
    private int mapId;
    private int spell1;
    private int spell2;
    private Stats stats;
    private String subType;
    private int teamId;


    public GameMode getGameMode() {
        return GameMode.getByName(gameMode);
    }

    public GameType getGameType() {
        return GameType.getByName(gameType);
    }

    public Queue getSubType() {
        return Queue.getByName(subType);
    }

    public Team getTeam() {
        return Team.getById(teamId);
    }
}

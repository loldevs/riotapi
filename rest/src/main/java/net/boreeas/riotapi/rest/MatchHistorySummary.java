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

package net.boreeas.riotapi.rest;

import lombok.AccessLevel;
import lombok.Getter;
import net.boreeas.riotapi.constants.Map;
import net.boreeas.riotapi.com.riotgames.platform.game.GameMode;

/**
 * Created on 4/14/2014.
 */
@Getter
public class MatchHistorySummary {
    private int death;
    private int assists;
    private int kills;
    private long date;
    private long gameId;
    private String gameMode;
    private boolean invalid;
    @Getter(AccessLevel.NONE) private int mapId;
    private int opposingTeamKills;
    private String opposingTeamName;
    private boolean win;

    private GameMode getGameMode() {
        return GameMode.getByName(gameMode);
    }

    private Map getMap() {
        return Map.getById(mapId);
    }
}

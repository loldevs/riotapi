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

package net.boreeas.riotapi.spectator.rest;

import lombok.Getter;
import net.boreeas.riotapi.constants.PlayerSide;


/**
 * Created on 4/28/2014.
 */
@Getter
public class FeaturedPlayer {
    private boolean bot;
    private int championId;
    /**
     * The team id of this player (100 for blue team, 200 for purple team)
     */
    private int teamId;
    /**
     * Id of the first summoner spell
     */
    private int spell1Id;
    /**
     * Id of the second summoner spell
     */
    private int spell2Id;
    /**
     * The skin this player is using
     */
    private int skinIndex;
    private int profileIconId;
    private String summonerName;

    public PlayerSide getTeam() {
        return PlayerSide.getById(teamId);
    }
}

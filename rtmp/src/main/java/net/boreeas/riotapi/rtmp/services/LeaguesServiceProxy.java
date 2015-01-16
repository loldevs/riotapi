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

package net.boreeas.riotapi.rtmp.services;

import lombok.AllArgsConstructor;
import net.boreeas.riotapi.com.riotgames.leagues.pojo.LeagueList;
import net.boreeas.riotapi.com.riotgames.platform.game.QueueType;
import net.boreeas.riotapi.com.riotgames.platform.leagues.client.dto.SummonerLeagueItems;
import net.boreeas.riotapi.com.riotgames.platform.leagues.client.dto.SummonerLeagues;
import net.boreeas.riotapi.rtmp.RtmpClient;

/**
 * Retrieve ranked stats
 */
@AllArgsConstructor
public class LeaguesServiceProxy {
    public static final String SERVICE = "leaguesServiceProxy";
    private RtmpClient client;

    /**
     * Retrieve the ranked position of the current player
     * @return Ranked positions
     */
    public SummonerLeagueItems getMyLeaguePositions() {
        return client.sendRpcAndWait(SERVICE, "getMyLeaguePositions");
    }

    /**
     * Retrieve the challenger league info for the target queue. Generally, this is one of
     * <ul>
     *     <li>{@link net.boreeas.riotapi.com.riotgames.platform.game.QueueType#RANKED_SOLO_5x5}</li>
     *     <li>{@link net.boreeas.riotapi.com.riotgames.platform.game.QueueType#RANKED_TEAM_3x3}</li>
     *     <li>{@link net.boreeas.riotapi.com.riotgames.platform.game.QueueType#RANKED_TEAM_5x5}</li>
     * </ul>
     * @param type The queue
     * @return The league info
     */
    public LeagueList getChallengerLeague(QueueType type) {
        return client.sendRpcAndWait(SERVICE, "getChallengerLeague", type);
    }

    /**
     * Retrieve all leagues for the current player
     * @return League info
     */
    public SummonerLeagues getAllMyLeagues() {
        return client.sendRpcAndWait(SERVICE, "getAllMyLeagues");
    }

    /**
     * Retrieve the leagues for the target team
     * @param teamName The team name
     * @return League info
     */
    public SummonerLeagues getLeaguesForTeam(String teamName) {
        return client.sendRpcAndWait(SERVICE, "getLeaguesForTeam", teamName);
    }

    /**
     * Retrieve the leagues for the target player
     * @param summonerId The player's id
     * @return League info
     */
    public SummonerLeagues getAllLeaguesForPlayer(long summonerId) {
        return client.sendRpcAndWait(SERVICE, "getAllLeaguesForPlayer", summonerId);
    }
}

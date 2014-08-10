/*
 * Copyright 2014 Malte Schütze
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
import net.boreeas.riotapi.com.riotgames.platform.leagues.client.dto.SummonerLeagueItems;
import net.boreeas.riotapi.com.riotgames.platform.leagues.client.dto.SummonerLeagues;
import net.boreeas.riotapi.QueueType;
import net.boreeas.riotapi.rtmp.RtmpClient;

/**
 * Created by malte on 7/16/2014.
 */
@AllArgsConstructor
public class LeaguesServiceProxy {
    public static final String SERVICE = "leaguesServiceProxy";
    private RtmpClient client;

    public SummonerLeagueItems getMyLeaguePositions() {
        return client.sendRpcAndWait(SERVICE, "getMyLeaguePositions");
    }

    public LeagueList getChallengerLeague(QueueType type) {
        return client.sendRpcAndWait(SERVICE, "getChallengerLeague", type);
    }

    public SummonerLeagues getAllMyLeagues() {
        return client.sendRpcAndWait(SERVICE, "getAllMyLeagues");
    }

    public SummonerLeagues getLeaguesForTeam(String teamName) {
        return client.sendRpcAndWait(SERVICE, "getLeaguesForTeam", teamName);
    }

    public SummonerLeagues getAllLeaguesForPlayer(double summonerId) {
        return client.sendRpcAndWait(SERVICE, "getAllLeaguesForPlayer", summonerId);
    }
}

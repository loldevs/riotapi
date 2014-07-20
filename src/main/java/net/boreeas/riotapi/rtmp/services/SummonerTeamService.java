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
import net.boreeas.riotapi.com.riotgames.platform.gameinvite.contract.Player;
import net.boreeas.riotapi.com.riotgames.team.TeamId;
import net.boreeas.riotapi.com.riotgames.team.dto.Team;
import net.boreeas.riotapi.rtmp.RtmpClient;

/**
 * Created by malte on 7/18/2014.
 */
@AllArgsConstructor
public class SummonerTeamService {
    private RtmpClient client;

    public Player createPlayer() {
        return client.sendRpcAndWait("summonerTeamService", "createPlayer");
    }

    public Team findTeamById(TeamId teamId) {
        return client.sendRpcAndWait("summonerTeamService", "findTeamById", teamId);
    }

    public Team findTeamByName(String name) {
        return client.sendRpcAndWait("summonerTeamService", "findTeamByName", name);
    }

    public Object disbandTeam(TeamId teamId) {
        return client.sendRpcAndWait("summonerTeamService", "disbandTeam", teamId);
    }

    public boolean isNameValidAndAvailable(String name) {
        return client.sendRpcAndWait("summonerTeamService", "isNameValidAndAvailable", name);
    }

    public boolean isTagValidAndAvailable(String tag) {
        return client.sendRpcAndWait("summonerTeamService", "isTagValidAndAvailable", tag);
    }

    public Team createTeam(String name, String tag) {
        return client.sendRpcAndWait("summonerTeamService", "createTeam", name, tag);
    }

    public Team invitePlayer(double summonerId, TeamId teamId) {
        return client.sendRpcAndWait("summonerTeamService", "invitePlayer", summonerId, teamId);
    }

    public Team kickPlayer(double summonerId, TeamId teamId) {
        return client.sendRpcAndWait("summonerTeamService", "kickPlayer", summonerId, teamId);
    }
}

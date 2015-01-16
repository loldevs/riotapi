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
import net.boreeas.riotapi.com.riotgames.platform.gameinvite.contract.Player;
import net.boreeas.riotapi.com.riotgames.team.TeamId;
import net.boreeas.riotapi.com.riotgames.team.dto.Team;
import net.boreeas.riotapi.rtmp.RtmpClient;

import javax.annotation.Nonnull;

/**
 * Created by malte on 7/18/2014.
 */
@AllArgsConstructor
@Nonnull
public class SummonerTeamService {
    public static final String SERVICE = "summonerTeamService";
    private RtmpClient client;

    public Player createPlayer() {
        return client.sendRpcAndWait(SERVICE, "createPlayer");
    }

    public Team findTeamById(TeamId teamId) {
        return client.sendRpcAndWait(SERVICE, "findTeamById", teamId);
    }

    public Team findTeamByName(String name) {
        return client.sendRpcAndWait(SERVICE, "findTeamByName", name);
    }

    public Object disbandTeam(TeamId teamId) {
        return client.sendRpcAndWait(SERVICE, "disbandTeam", teamId);
    }

    public boolean isNameValidAndAvailable(String name) {
        return client.sendRpcAndWait(SERVICE, "isNameValidAndAvailable", name);
    }

    public boolean isTagValidAndAvailable(String tag) {
        return client.sendRpcAndWait(SERVICE, "isTagValidAndAvailable", tag);
    }

    public Team createTeam(String name, String tag) {
        return client.sendRpcAndWait(SERVICE, "createTeam", name, tag);
    }

    public Team invitePlayer(double summonerId, TeamId teamId) {
        return client.sendRpcAndWait(SERVICE, "invitePlayer", summonerId, teamId);
    }

    public Team kickPlayer(double summonerId, TeamId teamId) {
        return client.sendRpcAndWait(SERVICE, "kickPlayer", summonerId, teamId);
    }
}

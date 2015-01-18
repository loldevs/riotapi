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
import net.boreeas.riotapi.com.riotgames.team.TeamId;
import net.boreeas.riotapi.com.riotgames.team.dto.Player;
import net.boreeas.riotapi.com.riotgames.team.dto.Team;
import net.boreeas.riotapi.rtmp.RtmpClient;

import javax.annotation.Nonnull;

/**
 * Handle ranked team creation
 */
@AllArgsConstructor
@Nonnull
public class SummonerTeamService {
    public static final String SERVICE = "summonerTeamService";
    private RtmpClient client;

    /**
     * Create a ranked team player?
     * @return The created player?
     */
    public Player createPlayer() {
        return client.sendRpcAndWait(SERVICE, "createPlayer");
    }

    /**
     * Find a team by their unique id
     * @param teamId The team id
     * @return The team
     */
    public Team findTeamById(TeamId teamId) {
        return client.sendRpcAndWait(SERVICE, "findTeamById", teamId);
    }

    /**
     * Find a team by their name
     * @param name The team name
     * @return The team
     */
    public Team findTeamByName(String name) {
        return client.sendRpcAndWait(SERVICE, "findTeamByName", name);
    }

    /**
     * Disband a ranked team
     * @param teamId The id of the team
     * @return unknown
     */
    public Object disbandTeam(TeamId teamId) {
        return client.sendRpcAndWait(SERVICE, "disbandTeam", teamId);
    }

    /**
     * Check if a name for a ranked team could be picked
     * @param name The name
     * @return <code>true</code> if the name could be picked
     */
    public boolean isNameValidAndAvailable(String name) {
        return client.sendRpcAndWait(SERVICE, "isNameValidAndAvailable", name);
    }

    /**
     * Check if a tag for a ranked team could be picked
     * @param tag The tag
     * @return <code>true</code> if the tag could be picked
     */
    public boolean isTagValidAndAvailable(String tag) {
        return client.sendRpcAndWait(SERVICE, "isTagValidAndAvailable", tag);
    }

    /**
     * Create a new ranked team with the specified name and tag
     * @param name The name
     * @param tag The tag
     * @return The created team
     */
    public Team createTeam(String name, String tag) {
        return client.sendRpcAndWait(SERVICE, "createTeam", name, tag);
    }

    /**
     * Invite a player to the target team
     * @param summonerId The id of the player
     * @param teamId The id of the team
     * @return The new team state
     */
    public Team invitePlayer(long summonerId, TeamId teamId) {
        return client.sendRpcAndWait(SERVICE, "invitePlayer", summonerId, teamId);
    }

    /**
     * Kick a player from the target team
     * @param summonerId The id of the player
     * @param teamId The id of the team
     * @return The new team state
     */
    public Team kickPlayer(long summonerId, TeamId teamId) {
        return client.sendRpcAndWait(SERVICE, "kickPlayer", summonerId, teamId);
    }
}

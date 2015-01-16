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
import net.boreeas.riotapi.com.riotgames.platform.statistics.*;
import net.boreeas.riotapi.com.riotgames.platform.statistics.team.TeamAggregatedStats;
import net.boreeas.riotapi.com.riotgames.platform.summoner.SummonerSkillLevel;
import net.boreeas.riotapi.com.riotgames.team.TeamId;
import net.boreeas.riotapi.com.riotgames.platform.game.GameMode;
import net.boreeas.riotapi.constants.Season;
import net.boreeas.riotapi.rtmp.RtmpClient;

import java.util.List;

/**
 * Retrieve player stats. New accounts need to call {@link #processEloQuestionaire(net.boreeas.riotapi.com.riotgames.platform.summoner.SummonerSkillLevel)} here
 */
@AllArgsConstructor
public class PlayerStatsService {
    public static final String SERVICE = "playerStatsService";
    private RtmpClient client;

    /**
     * Set the skill level for this account
     * @param skill The skill level
     * @return unknown
     */
    public Object processEloQuestionaire(SummonerSkillLevel skill) {
        return client.sendRpcAndWait(SERVICE, "processEloQuestionaire", skill);
    }

    /**
     * Retrieve player stats
     * @param accountId The player's id
     * @param season The target season
     * @return Player stats
     */
    public PlayerLifetimeStats retrievePlayerStatsByAccountId(long accountId, Season season) {
        return client.sendRpcAndWait(SERVICE, "retrievePlayerStatsByAccountId", accountId, season);
    }

    /**
     * Retrieve the most played champions for the target player
     * @param accId The player's id
     * @param mode The mode to check
     * @return Champion stats
     */
    public List<ChampionStatInfo> retrieveTopPlayedChampions(long accId, GameMode mode) {
        return client.sendRpcAndWait(SERVICE, "retrieveTopPlayedChampions", accId, mode);
    }

    /**
     * Retrieve a player's stats
     * @param id The id of the player
     * @param gameMode The game mode to check
     * @param season The season to check
     * @return Stats
     */
    public AggregatedStats getAggregatedStats(long id, GameMode gameMode, Season season) {
        return client.sendRpcAndWait(SERVICE, "getAggregatedStats", id, gameMode, season.numeric);
    }

    /**
     * Retrieve recently played games for the target player
     * @param accId The id of the player
     * @return The recent games
     */
    public RecentGames getRecentGames(long accId) {
        return client.sendRpcAndWait(SERVICE, "getRecentGames", accId);
    }

    /**
     * Retrieve stats for a team
     * @param teamId The id of the team
     * @return The team stats
     */
    public List<TeamAggregatedStats> getTeamAggregatedStats(TeamId teamId) {
        return client.sendRpcAndWait(SERVICE, "getTeamAggregatedStats", teamId);
    }

    /**
     * Retrieve post-game stats for a team
     * @param teamId The id of the team
     * @param gameId The if of the game
     * @return Post-game stats
     */
    public EndOfGameStats getTeamEndOfGameStats(TeamId teamId, long gameId) {
        return client.sendRpcAndWait(SERVICE, "getTeamEndOfGameStats", teamId, gameId);
    }
}
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

package net.boreeas.riotapi.com.riotgames.platform.statistics;

import lombok.Data;
import net.boreeas.riotapi.com.riotgames.team.TeamInfo;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/20/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.statistics.EndOfGameStats")
public class EndOfGameStats {
    private int talentPointsGained;
    private boolean ranked;
    private boolean leveledUp;
    private int skinIndex;
    private int queueBonusEarned;
    private String gameType;
    private long experienceEarned;
    private boolean imbalancedTeamsNoPoints;
    private List<PlayerParticipantStatsSummary> teamPlayerParticipantStats = new ArrayList<>();
    private List<PlayerParticipantStatsSummary> otherTeamPlayerParticipantStats = new ArrayList<>();
    private int basePoints;
    private int reportGameId;
    private String difficulty;
    private long gameLength;
    private long boostXpEarned;
    private boolean invalid;
    private TeamInfo otherTeamInfo;
    private TeamInfo myTeamInfo;
    private String roomName;
    private int customMinutesLeft;
    private int userId;
    // TODO inspect
    private List<Object> pointsPenalties = new ArrayList<>();
    private int coOpVsAiMinutesLeftToday;
    private long loyaltyBoostIpEarned;
    private long rpEarned;
    private int completionBonusPoints;
    private long coOpVsAiMsecsUntilReset;
    private long boostIpEarned;
    // TODO inspect
    private List<Object> newSpells = new ArrayList<>();
    private long experienceTotal;
    private long gameId;
    private long timeUntilNextFirstWinBonus;
    private long loyaltyBoostXpEarned;
    private String roomPassword;
    private int elo;
    private long ipEarned;
    private long firstWinBonus;
    private boolean sendStatsToTournamentProvider;
    private int eloChange;
    private String gameMode;
    private String queueType;
    private int odinBonusIp;
    private String myTeamStatus;
    private long ipTotal;
    private String summonerName;
    private long customMsecUntilReset;
    private long rerollPointsEarned;
}

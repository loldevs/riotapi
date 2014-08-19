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

import lombok.Getter;

/**
 * @author Malte Schütze
 */
@Getter
public class ParticipantStats {
    private long champLevel;
    /**
     * If game was a dominion game, player's combat score, otherwise 0.
     */
    private long combatPlayerScore;

    private long kills;
    private long deaths;
    private long assists;

    private long doubleKills;
    private long tripleKills;
    private long quadraKills;
    private long pentaKills;
    private long unrealKills;

    private boolean firstBloodAssist;
    private boolean firstBloodKill;
    private boolean firstInhibitorAssist;
    private boolean firstInhibitorKill;
    private boolean firstTowerAssist;
    private boolean firstTowerKill;

    private long goldEarned;
    private long goldSpent;

    private long inhibitorKills;

    private long item0;
    private long item1;
    private long item2;
    private long item3;
    private long item4;
    private long item5;
    private long item6;

    private long killingSprees;
    private long largestCriticalStrike;
    private long largestKillingSpree;
    private long largestMultiKill;

    private long magicDamageDealt;
    private long magicDamageDealtToChampions;
    private long magicDamageTaken;

    private long physicalDamageDealt;
    private long physicalDamageDealtToChampions;
    private long physicalDamageTaken;

    private long trueDamageDealt;
    private long trueDamageDealtToChampions;
    private long trueDamageTaken;

    private long totalDamageDealt;
    private long totalDamageDealtToChampions;
    private long totalDamageTaken;

    private long neutralMinionsKilled;
    private long neutralMinionsKilledEnemyJungle;
    private long neutralMinionsKilledTeamJungle;

    /**
     * If game was a dominion game, number of node captures.
     */
    private long nodeCapture;
    /**
     * If game was a dominion game, number of node capture assists.
     */
    private long nodeCaptureAssist;
    /**
     * If game was a dominion game, number of node neutralizations.
     */
    private long nodeNeutralize;
    /**
     * If game was a dominion game, number of node neutralization assists.
     */
    private long nodeNeutralizeAssist;
    /**
     * If game was a dominion game, player's objectives score, otherwise 0.
     */
    private long objectivePlayerScore;
    /**
     * If game was a dominion game, number of completed team objectives (i.e., quests).
     */
    private long teamObjective;
    /**
     * If game was a dominion game, player's total score, otherwise 0.
     */
    private long totalPlayerScore;
    /**
     * If game was a dominion game, team rank of the player's total score (e.g., 1-5).
     */
    private long totalScoreRank;

    private long totalHeal;
    private long totalUnitsHealed;

    private long sightWardsBoughtInGame;
    private long visionWardsBoughtInGame;
    private long wardsKilled;
    private long wardsPlaced;

    private long totalTimeCrowdControlDealt;
    private long towerKills;

    private boolean winner;
}

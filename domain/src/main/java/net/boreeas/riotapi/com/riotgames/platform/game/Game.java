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

package net.boreeas.riotapi.com.riotgames.platform.game;

import lombok.Data;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/19/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.game.GameDTO")
public class Game {
    /**
     * Known values: ALL
     */
    private String spectatorsAllowed;
    private boolean passwordSet;
    private GameType gameType;
    /**
     * See LoginDataPacket.gameTypeConfigs for more information
     */
    private int gameTypeConfigId;
    // TODO inspect
    private Object glmGameId;
    /**
     * Known values: TEAM_SELECT, CHAMP_SELECT
     */
    private String gameState;
    private String gameStateString;
    // TODO inspect
    private Object glmGameHost;
    private Object glmHost;
    private int glmPort;
    private int glmSecurePort;
    private List<GameObserver> observers = new ArrayList<>();
    // TODO inspect
    private Object statusOfParticipants;
    private long id;
    private PlayerParticipant ownerSummary;
    private List<Participant> teamOne = new ArrayList<>();
    private List<Participant> teamTwo = new ArrayList<>();
    private List<BannedChampion> bannedChampions = new ArrayList<>();
    private String roomName;
    private String name;
    private int spectatorDelay;
    private String terminateCondition;
    /**
     * Queue type or NONE
     */
    private String queueTypeName;
    // TODO inspect
    private Object passbackUrl;
    private String roomPassword;
    private long optimisticLock;
    private int maxNumPlayers;
    private int queuePosition;
    private GameMode gameMode;
    private long expiryTime;
    private int mapId;
    private List<Integer> banOrder = new ArrayList<>();
    private int pickTurn;
    private List<PlayerChampionSelection> playerChampionSelections = new ArrayList<>();
    private int joinTimerDuration;
    // TODO inspect
    private Object passbackDataPacket;
    /**
     * Known values: INSUFFICIENT_PLAYERS, UNBALANCED_TEAMS
     */
    private List<String> practiceGameRewardsDisabledReasons = new ArrayList<>();
    private Object teamOnePickOutcome;
    private Object teamTwoPickOutcome;
    /**
     * Known values: NOT_TERMINATED
     */
    private String terminatedCondition;
    private String terminatedConditionString;
    private FeaturedGameInfo featuredGameInfo;
    private List<?> gameMutators = new ArrayList<>();
}

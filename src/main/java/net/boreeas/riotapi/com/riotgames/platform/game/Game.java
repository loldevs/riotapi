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
    private String spectatorsAllowed;
    private boolean passwordSet;
    private String gameType;
    private int gameTypeConfigId;
    // TODO inspect
    private Object glmGameId;
    private String gameState;
    // TODO inspect
    private Object glmGameHost;
    private int glmPort;
    private int glmSecurePort;
    private List<GameObserver> observers = new ArrayList<>();
    // TODO inspect
    private Object statusOfParticipants;
    private double id;
    private PlayerParticipant ownerSummary;
    private List<Participant> teamOnw = new ArrayList<>();
    private List<Participant> teamTwo = new ArrayList<>();
    private List<BannedChampion> bannedChampions = new ArrayList<>();
    private String roomName;
    private String name;
    private int spectatorDelay;
    private String terminateCondition;
    private String queueTypeName;
    // TODO inspect
    private Object passbackUrl;
    private String roomPassword;
    private double optimisticLock;
    private int maxNumPlayers;
    private int queuePosition;
    private String gameMode;
    private double expiryTime;
    private int mapId;
    private List<Integer> banOrder = new ArrayList<>();
    private int pickTurn;
    private String gameStateString;
    private List<PlayerChampionSelection> playerChampionSelections = new ArrayList<>();
    private int joinTimerDuration;
    // TODO inspect
    private Object passbackDataPacket;
}

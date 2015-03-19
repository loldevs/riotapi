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

package net.boreeas.riotapi.com.riotgames.platform.game.practice;

import lombok.Data;
import net.boreeas.riotapi.com.riotgames.platform.game.PlayerParticipant;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;

/**
 * Created on 7/19/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.game.practice.PracticeGameSearchResult")
public class PracticeGameSearchResult {
    private int spectatorCount;
    // TODO inspect
    private Object glmGameId;
    // TODO inspect
    private Object glmHost;
    private int glmPort;
    private ArrayList<Object> gameMutators = new ArrayList<>();
    private Object glmGameHost;
    private int glmGamePort;
    private String gameModeString;
    private String allowSpectators;
    private int gameMapId;
    private int maxNumPlayers;
    private int glmSecurePort;
    private String gameMode;
    private long id;
    private String name;
    private boolean privateGame;
    private PlayerParticipant owner;
    private int team1Count;
    private int team2Count;
}

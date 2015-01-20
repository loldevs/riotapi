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
import net.boreeas.riotapi.com.riotgames.platform.game.map.GameMap;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/19/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.game.PracticeGameConfig")
public class PracticeGameConfig {
    // TODO inspect
    private Object passbackUrl;
    private String gameName;
    private int gameTypeConfig;
    // TODO inspect
    private Object passbackDataPacket;
    private String gamePassword;
    private GameMap gameMap;
    private String gameMode;
    private String allowSpectators;
    private int maxNumPlayers;
    private String region;
    private List<?> gameMutators = new ArrayList<>();
}

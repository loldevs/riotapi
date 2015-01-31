/*
 * Copyright 2015 The LolDevs team (https://github.com/loldevs)
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

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.com.riotgames.platform.game.BannedChampion;
import net.boreeas.riotapi.com.riotgames.platform.game.GameMode;
import net.boreeas.riotapi.com.riotgames.platform.game.GameType;

import java.util.ArrayList;
import java.util.List;

/**
 * In-progress game info from the rest api
 * @author Malte Schütze
 */
@Data
public class CurrentGameInfo {
    private List<BannedChampion> bannedChampions = new ArrayList<>();
    private long gameId;
    private long gameLength;
    private GameMode gameMode;
    private long gameQueueConfigId;
    private long gameStartTime;
    private GameType gameType;
    private int mapId;
    private int gameTypeConfigId;
    @Getter(AccessLevel.NONE) private ObserverData observers;
    private List<InProgressGameParticipant> participants = new ArrayList<>();
    private String platformId;

    public String getEncryptionKey() {
        return observers.encryptionKey;
    }

    /**
     * For some reason there's a single-field object in the json
     */
    private class ObserverData {
        private String encryptionKey;
    }

    public Shard getPlatformId() {
        return Shard.getBySpectatorPlatform(platformId);
    }
}

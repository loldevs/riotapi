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

package net.boreeas.riotapi.spectator.rest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.com.riotgames.platform.game.BannedChampion;
import net.boreeas.riotapi.com.riotgames.platform.game.GameMode;
import net.boreeas.riotapi.com.riotgames.platform.game.GameType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeaturedGame {
    private long gameId;
    private int mapId;
    private GameMode gameMode;
    private GameType gameType;
    private int gameQueueConfigId;
    private int gameTypeConfigId;
    private List<FeaturedPlayer> participants = new ArrayList<>();
    @Getter(AccessLevel.NONE) private ObserverData observers;
    private String platformId;
    private List<BannedChampion> bannedChampions = new ArrayList<>();
    private long gameStartTime;
    private long gameLength;

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

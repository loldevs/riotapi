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
import net.boreeas.riotapi.com.riotgames.platform.summoner.icon.SummonerIconInventory;
import net.boreeas.riotapi.rtmp.RtmpClient;

/**
 * Manage summoner icons
 */
@AllArgsConstructor
public class SummonerIconService {
    public static final String SERVICE = "summonerIconService";
    private RtmpClient client;

    /**
     * Retrieve the targets owned icons
     * @param summonerId The id of the player
     * @return The player's icons
     */
    public SummonerIconInventory getSummonerIconInventory(long summonerId) {
        return client.sendRpcAndWait(SERVICE, "getSummonerIconInventory", summonerId);
    }


}

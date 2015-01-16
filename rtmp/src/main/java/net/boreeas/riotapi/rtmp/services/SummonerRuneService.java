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
import net.boreeas.riotapi.com.riotgames.platform.summoner.runes.SummonerRuneInventory;
import net.boreeas.riotapi.rtmp.RtmpClient;

/**
 * Manage runes
 */
@AllArgsConstructor
public class SummonerRuneService {
    public static final String SERVICE = "summonerRuneService";
    private RtmpClient client;

    /**
     * Retrieve all owned runes
     * @param summonerId The id of the target player
     * @return The player's runes
     */
    public SummonerRuneInventory getSummonerRuneInventory(long summonerId) {
        return client.sendRpcAndWait(SERVICE, "getSummonerRuneInventory", summonerId);
    }
}

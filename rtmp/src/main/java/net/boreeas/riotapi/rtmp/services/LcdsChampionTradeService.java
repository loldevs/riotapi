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
import net.boreeas.riotapi.com.riotgames.platform.trade.api.contract.PotentialTraders;
import net.boreeas.riotapi.rtmp.RtmpClient;

/**
 * This service handles trade attmempts in draft and aram.
 */
@AllArgsConstructor
public class LcdsChampionTradeService {
    public static final String SERVICE = "lcdsChampionTradeService";
    private RtmpClient client;

    /**
     * List all players with whom a trade could be attempted
     * @return The potential traders
     */
    public PotentialTraders getPotentialTraders() {
        return client.sendRpcAndWait(SERVICE, "getPotentialTraders");
    }

    /**
     * Attempt to trade with the target player
     * @param summonerInternalName The summoner's internal name, as sent by {@link #getPotentialTraders()}
     * @param championId Unknown - id of sent champion? id of trade?
     * @return unknown
     */
    public Object attemptTrade(String summonerInternalName, int championId) {
        return client.sendRpcAndWait(SERVICE, "attemptTrade", summonerInternalName, championId, false);
    }

    /**
     * Dismiss an attempted trade
     * @return unknown
     */
    public Object dismissTrade() {
        return client.sendRpcAndWait(SERVICE, "dismissTrade");
    }

    /**
     * Accept a trade
     * @param summonerInternalName The summoner's internal name
     * @param championId unknown - The id of the sent champion?
     * @return unknown
     */
    public Object acceptTrade(String summonerInternalName, int championId) {
        return client.sendRpcAndWait(SERVICE, "attemptTrade", summonerInternalName, championId, true);
    }


}

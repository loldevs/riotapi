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

package net.boreeas.riotapi.rtmp.services;

import lombok.AllArgsConstructor;
import net.boreeas.riotapi.com.riotgames.platform.trade.api.contract.PotentialTraders;
import net.boreeas.riotapi.rtmp.RtmpClient;

/**
 * Created by malte on 7/18/2014.
 */
@AllArgsConstructor
public class LcdsChampionTradeService {
    private static final String SERVICE = "lcdsChampionTradeService";
    private RtmpClient client;

    public PotentialTraders getPotentialTraders() {
        return client.sendRpcAndWait(SERVICE, "getPotentialTraders");
    }

    public Object attemptTrade(String summonerInternalName, int championId) {
        return client.sendRpcAndWait(SERVICE, "attemptTrade", summonerInternalName, championId, false);
    }

    public Object dismissTrade() {
        return client.sendRpcAndWait(SERVICE, "dismissTrade");
    }

    public Object acceptTrade(String summonerInternalName, int championId) {
        return client.sendRpcAndWait(SERVICE, "attemptTrade", summonerInternalName, championId, true);
    }


}

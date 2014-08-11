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
import net.boreeas.riotapi.com.riotgames.platform.summoner.AllPublicSummonerData;
import net.boreeas.riotapi.com.riotgames.platform.summoner.AllSummonerData;
import net.boreeas.riotapi.com.riotgames.platform.summoner.PublicSummoner;
import net.boreeas.riotapi.rtmp.RtmpClient;

import java.util.List;

/**
 * Created by malte on 7/18/2014.
 */
@AllArgsConstructor
public class SummonerService {
    private static final String SERVICE = "summonerService";
    private RtmpClient client;


    public AllSummonerData getAllSummonerDataByAccount(double accId) {
        return client.sendRpcAndWait(SERVICE, "getAllSummonerDataByAccount", accId);
    }

    public PublicSummoner getSummonerByName(String name) {
        return client.sendRpcAndWait(SERVICE, "getSummonerByName", name);
    }

    public AllPublicSummonerData getAllPublicSummonerDataByAccount(double accId) {
        return client.sendRpcAndWait(SERVICE, "getAllPublicSummonerDataByAccount", accId);
    }

    public String getSummonerInternalNameByName(String name) {
        return client.sendRpcAndWait(SERVICE, "getSummonerInternalNameByName", name);
    }

    public Object updateProfileIconId(long iconId) {
        return client.sendRpcAndWait(SERVICE, "updateProfileIconId", iconId);
    }

    public List<String> getSummonerNames(double... summonerIds) {
        return client.sendRpcAndWait(SERVICE, "getSummonerNames", summonerIds);
    }

    public AllSummonerData createDefaultSummoner(String name) {
        return client.sendRpcAndWait(SERVICE, "createDefaultSummoner", name);
    }
}

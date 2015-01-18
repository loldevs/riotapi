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
 * Retrive player stats. New accounts need to call {@link #createDefaultSummoner} here
 */
@AllArgsConstructor
public class SummonerService {
    public static final String SERVICE = "summonerService";
    private RtmpClient client;

    /**
     * Retrieve summoner information
     * @param accId The target account id
     * @return Information
     */
    public AllSummonerData getAllSummonerDataByAccount(long accId) {
        return client.sendRpcAndWait(SERVICE, "getAllSummonerDataByAccount", accId);
    }

    /**
     * Retrieve a summoner by their public name
     * @param name The name of the player
     * @return Public information of the target player
     */
    public PublicSummoner getSummonerByName(String name) {
        return client.sendRpcAndWait(SERVICE, "getSummonerByName", name);
    }

    /**
     * Retrieve public summoner information
     * @param accId The target account id
     * @return The information
     */
    public AllPublicSummonerData getAllPublicSummonerDataByAccount(long accId) {
        return client.sendRpcAndWait(SERVICE, "getAllPublicSummonerDataByAccount", accId);
    }

    /**
     * Retrieve a summoner's internal name by their public name
     * @param name The target's name
     * @return The internal name
     */
    public String getSummonerInternalNameByName(String name) {
        return client.sendRpcAndWait(SERVICE, "getSummonerInternalNameByName", name);
    }

    /**
     * Update your profile icon
     * @param iconId The id of the icon
     * @return unknown
     */
    public Object updateProfileIconId(long iconId) {
        return client.sendRpcAndWait(SERVICE, "updateProfileIconId", iconId);
    }

    /**
     * Retrieve summoner names for the target ids
     * @param summonerIds The ids of the players
     * @return Their names
     */
    public List<String> getSummonerNames(long... summonerIds) {
        return client.sendRpcAndWait(SERVICE, "getSummonerNames", summonerIds);
    }

    /**
     * Create a new summoner with the target display name
     * @param name The display name
     * @return The new summoner's information
     */
    public AllSummonerData createDefaultSummoner(String name) {
        return client.sendRpcAndWait(SERVICE, "createDefaultSummoner", name);
    }
}

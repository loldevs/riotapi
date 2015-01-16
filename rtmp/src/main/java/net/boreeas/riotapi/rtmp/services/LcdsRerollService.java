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
import net.boreeas.riotapi.com.riotgames.platform.reroll.pojo.PointSummary;
import net.boreeas.riotapi.com.riotgames.platform.reroll.pojo.RollResult;
import net.boreeas.riotapi.rtmp.RtmpClient;

/**
 * Handle ARAM rerolls
 */
@AllArgsConstructor
public class LcdsRerollService {
    public static final String SERVICE = "lcdsRerollService";
    private RtmpClient client;

    /**
     * Get the currently available reroll points
     * @return The points
     */
    public PointSummary getPointsBalance() {
        return client.sendRpcAndWait(SERVICE, "getPointsBalance");
    }

    /**
     * Reroll your champion
     * @return The result of the roll
     */
    public RollResult roll() {
        return client.sendRpcAndWait(SERVICE, "roll");
    }
}

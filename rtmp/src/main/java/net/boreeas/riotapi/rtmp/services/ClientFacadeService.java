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
import net.boreeas.riotapi.com.riotgames.platform.clientfacade.domain.LoginDataPacket;
import net.boreeas.riotapi.com.riotgames.platform.harassment.LcdsResponseString;
import net.boreeas.riotapi.rtmp.RtmpClient;

/**
 * Some account management stuff. Purpose not 100% known
 */
@AllArgsConstructor
public class ClientFacadeService {
    public static final String SERVICE = "clientFacadeService";
    private RtmpClient client;

    /**
     * Retrieve the login data for the current user
     * @return The login data packet
     */
    public LoginDataPacket getLoginDataPacket() {
        return client.sendRpcAndWait(SERVICE, "getLoginDataPacketForUser");
    }

    /**
     * Call the kudos service
     * @param json kudos data
     * @return Some response
     */
    public LcdsResponseString callKudos(String json) {
        return client.sendRpcAndWait(SERVICE, "callKudos", json);
    }
}

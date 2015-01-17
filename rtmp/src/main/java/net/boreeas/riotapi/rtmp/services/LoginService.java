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

import net.boreeas.riotapi.com.riotgames.platform.login.Session;
import net.boreeas.riotapi.rtmp.RtmpClient;
import net.boreeas.riotapi.com.riotgames.platform.login.AuthenticationCredentials;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Logging in and authenticating, as well as performing regular pings
 */
public class LoginService {
    public static final String SERVICE = "loginService";
    private RtmpClient client;
    private SimpleDateFormat format = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss z");

    public LoginService(RtmpClient client) {
        this.client = client;
        this.format.setTimeZone(TimeZone.getTimeZone("PST"));
    }


    /**
     * Login to the rtmp server
     * @param credentials The user's credentials
     * @return Session information
     */
    public Session login(AuthenticationCredentials credentials) {
        return client.sendRpcAndWait(SERVICE, "login", credentials);
    }

    /**
     * Perform a heartbeat
     * @param accountId The user id
     * @param sessionToken The token for the current session
     * @param heartBeatCount The amount of heartbeats that have been sent
     * @return A string
     */
    public String performLcdsHeartBeat(long accountId, String sessionToken, int heartBeatCount) {
        return performLcdsHeartBeat(accountId, sessionToken, heartBeatCount, new Date());
    }

    /**
     * Perform a heartbeat
     * @param accountId The user id
     * @param sessionToken The token for the current session
     * @param heartBeatCount The amount of heartbeats that have been sent
     * @param date The time of the heart beat
     * @return A string
     */
    public String performLcdsHeartBeat(long accountId, String sessionToken, int heartBeatCount, Date date) {
        return client.sendRpcAndWait(SERVICE, "performLCDSHeartBeat", accountId, sessionToken, heartBeatCount, format.format(date));
    }

    /**
     * Retrieves the store url with auth token for the current user
     * @return The store url
     */
    public String getStoreUrl() {
        return client.sendRpcAndWait(SERVICE, "getStoreUrl");
    }
}

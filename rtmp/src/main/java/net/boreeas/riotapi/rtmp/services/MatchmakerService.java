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
import net.boreeas.riotapi.com.riotgames.platform.matchmaking.GameQueueConfig;
import net.boreeas.riotapi.com.riotgames.platform.matchmaking.MatchMakerParams;
import net.boreeas.riotapi.com.riotgames.platform.matchmaking.QueueInfo;
import net.boreeas.riotapi.com.riotgames.platform.matchmaking.SearchingForMatchNotification;
import net.boreeas.riotapi.rtmp.RtmpClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Complimentary to {@link net.boreeas.riotapi.rtmp.services.GameService}, this service handles joining
 * and leaving queues
 */
@AllArgsConstructor
@ParametersAreNonnullByDefault
public class MatchmakerService {
    public static final String SERVICE = "matchmakerService";
    private RtmpClient client;

    /**
     * Retrieves information for the specified queue
     * @param queueId The id for the queue
     * @return Information on the queue
     */
    public QueueInfo getQueueInfo(long queueId) {
        return client.sendRpcAndWait(SERVICE, "getQueueInfo", queueId);
    }

    /**
     * Attach a premade team to a queue
     * @param params The queue parameters
     * @return A notification indicating success
     */
    public SearchingForMatchNotification attachTeamToQueue(MatchMakerParams params) {
        return client.sendRpcAndWait(SERVICE, "attachTeamToQueue", params);
    }

    /**
     * (Probably) leave the current queues?
     * @return unknown
     */
    public Object purgeFromQueues() {
        return client.sendRpcAndWait(SERVICE, "purgeFromQueues");
    }

    /**
     * Attach the user to a queue
     * @param params The queue parameters
     * @return A notification for the result
     */
    public SearchingForMatchNotification attachToQueue(MatchMakerParams params) {
        return client.sendRpcAndWait(SERVICE, "attachToQueue", params);
    }

    /**
     * Attempts to leave a queue
     * @param summonerId The id of the summoner
     * @return <code>true</code> if successfully cancelled, otherwise cancelling isn't possible (i.e. champ select
     * imminent)
     */
    public boolean cancelFromQueueIfPossible(long summonerId) {
        return client.sendRpcAndWait(SERVICE, "cancelFromQueueIfPossible", summonerId);
    }

    /**
     * Retrieve a list of currently enabled queues
     * @return The list of queues
     */
    public List<GameQueueConfig> getAvailableQueues() {
        return client.sendRpcAndWait(SERVICE, "getAvailableQueues");
    }

    /**
     * See if matchmaking is enabled. Unknown if that means globally or to see if the player is in a queue.
     * @return <code>true</code> if enabled, <code>false</code> otherwise
     */
    public boolean isMatchmakingEnabled() {
        return client.sendRpcAndWait(SERVICE, "isMatchmakingEnabled");
    }


    @Deprecated
    public Object acceptInviteForMatchmakingGame(String inviteId) {
        return client.sendRpcAndWait(SERVICE, "acceptInviteForMatchmakingGame", inviteId);
    }
}

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
import net.boreeas.riotapi.com.riotgames.platform.gameinvite.contract.LobbyStatus;
import net.boreeas.riotapi.constants.BotDifficulty;
import net.boreeas.riotapi.rtmp.RtmpClient;

import java.util.List;

/**
 * The game invitation service manages group creation and joining for Group Finder and Premade Teans.
 */
@AllArgsConstructor
public class LcdsGameInvitationService {
    public static final String SERVICE = "lcdsGameInvitationService";
    private RtmpClient client;

    /**
     * Create a groupfinder lobby
     * @param queueId The target queue
     * @param uuid The uuid for this lobby
     * @return The lobby status
     */
    public LobbyStatus createGroupFinderLobby(long queueId, String uuid) {
        return client.sendRpcAndWait(SERVICE, "createGroupFinderLobby", queueId, uuid);
    }

    /**
     * Create a premade team lobby
     * @param queueId The target queue
     * @return The lobby status
     */
    public LobbyStatus createArrangedTeamLobby(long queueId) {
        return client.sendRpcAndWait(SERVICE, "createArrangedTeamLobby", queueId);
    }

    /**
     * Create a premade team lobby for bot games
     * @param queueId The target queue
     * @param difficulty The difficulty of the bots
     * @return The lobby status
     * @deprecated Use {@link #createArrangedBotTeamLobby(long, net.boreeas.riotapi.constants.BotDifficulty)}
     */
    @Deprecated
    public LobbyStatus createArrangedBotTeamLobby(long queueId, String difficulty) {
        return client.sendRpcAndWait(SERVICE, "createArrangedBotTeamLobby", queueId, difficulty);
    }

    /**
     * Create a premade team lobby for bot games
     * @param queueId The target queue
     * @param difficulty The difficulty of the bots
     * @return The lobby status
     */
    public LobbyStatus createArrangedBotTeamLobby(long queueId, BotDifficulty difficulty) {
        return client.sendRpcAndWait(SERVICE, "createArrangedBotTeamLobby", queueId, difficulty);
    }

    /**
     * Retrieve the lobby status for the current team lobby
     * @return The lobby status
     */
    public LobbyStatus checkLobbyStatus() {
        return client.sendRpcAndWait(SERVICE, "checkLobbyStatus");
    }

    /**
     * Retrieve all currently pending invitations. Invitations are pending if the user has neither accepted nor
     * declined.
     * @return The pending invitations
     */
    public List<Object> getPendingInvitations() {
        return client.sendRpcAndWait(SERVICE, "getPendingInvitations");
    }

    /**
     * Grant invite privileges to the target summoner
     * @param summonerId The id of the summoner
     */
    public void grantInvitePrivileges(long summonerId) {
        client.sendRpcToDefault(SERVICE, "grantInvitePrivileges", summonerId);
    }

    /**
     * Transfer lobby ownership to the target user
     * @param summonerId The id of the summoner
     */
    public void transferOwnership(long summonerId) {

        client.sendRpcToDefault(SERVICE, "transferOwnership", summonerId);
    }

    /**
     * Invite the target user to the current lobby.
     * @param summonerId The id of the summoner
     */
    public void invite(long summonerId) {
        client.sendRpcToDefault(SERVICE, "invite", summonerId);
    }

    /**
     * Leave the current lobby.
     */
    public void leave() {
        client.sendRpcToDefault(SERVICE, "leave");
    }

    /**
     * Accept a game invitation.
     * @param inviteId The id of the invitation (transmitted via {@link net.boreeas.riotapi.com.riotgames.platform.gameinvite.contract.InvitationRequest})
     * @return The lobby status
     */
    public LobbyStatus accept(String inviteId) {
        return client.sendRpcAndWait(SERVICE, "accept", inviteId);
    }

    /**
     * Decline an invitation
     * @param inviteId The id of the invitation (transmitted via {@link net.boreeas.riotapi.com.riotgames.platform.gameinvite.contract.InvitationRequest})
     */
    public void decline(String inviteId) {
        client.sendRpcToDefault(SERVICE, "decline", inviteId);
    }
}

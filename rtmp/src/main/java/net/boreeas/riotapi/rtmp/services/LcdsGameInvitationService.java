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
import net.boreeas.riotapi.com.riotgames.platform.gameinvite.contract.LobbyStatus;
import net.boreeas.riotapi.rtmp.RtmpClient;

import java.util.List;

/**
 * Created by malte on 7/18/2014.
 */
@AllArgsConstructor
public class LcdsGameInvitationService {
    private static final String SERVICE = "lcdsGameInvitationService";
    private RtmpClient client;

    public LobbyStatus createGroupFinderLobby(double gameMode, String uuid) {
        return client.sendRpcAndWait(SERVICE, "createGroupFinderLobby", gameMode, uuid);
    }

    public LobbyStatus createArrangedTeamLobby(double queueId) {
        return client.sendRpcAndWait(SERVICE, "createArrangedTeamLobby", queueId);
    }

    public LobbyStatus createArrangedBotTeamLobby(double queueId, String difficulty) {
        return client.sendRpcAndWait(SERVICE, "createArrangedTeamLobby", difficulty);
    }

    public LobbyStatus getLobbyStatus() {
        return client.sendRpcAndWait(SERVICE, "getLobbyStatus");
    }

    public List<Object> getPendingInvitations() {
        return client.sendRpcAndWait(SERVICE, "getPendingInvitations");
    }

    public void grantInvitePrivileges(double summonerId) {
        client.sendRpc(SERVICE, "grantInvitePrivileges", summonerId);
    }

    public void transferOwnership(double summonerId) {

        client.sendRpc(SERVICE, "transferOwnership", summonerId);
    }

    public void invite(double summonerId) {
        client.sendRpc(SERVICE, "invite", summonerId);
    }

    public void leave() {
        client.sendRpc(SERVICE, "leave");
    }

    public LobbyStatus accept(String inviteId) {
        return client.sendRpcAndWait(SERVICE, "decline", inviteId);
    }

    public void decline(String inviteId) {
        client.sendRpc(SERVICE, "decline", inviteId);
    }
}

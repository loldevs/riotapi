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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.boreeas.riotapi.com.riotgames.platform.gameinvite.contract.LobbyStatus;
import net.boreeas.riotapi.com.riotgames.platform.game.GameMode;
import net.boreeas.riotapi.rtmp.RtmpClient;

/**
 * Created by malte on 7/18/2014.
 */
@AllArgsConstructor
public class LcdsServiceProxy {
    private static final String SERVICE = "lcdsServiceProxy";
    private RtmpClient client;

    public LobbyStatus createGroupFinderLobby(int type, String uuid) {
        return client.sendRpcAndWait(SERVICE, "createGroupFinderLobby", type, uuid);
    }

    public Object call(String uuid, GameMode mode, String procCall, JsonObject object) {
        return client.sendRpcAndWait(SERVICE, "call", uuid, mode.name(), procCall, object.toString());
    }

    public LcdsHandle createHandle(String uuid, GameMode mode) {
        return new LcdsHandle(uuid, mode, this);
    }


    @AllArgsConstructor
    public static class LcdsHandle {
        private String uuid;
        private GameMode mode;
        private LcdsServiceProxy lcds;
        private final Gson gson = new Gson();

        public void acceptCandidate(double slotId) {
            JsonObject object = new JsonObject();
            object.addProperty("slotId", slotId);
            lcds.call(uuid, mode, "acceptCandidateV2", object);
        }

        public void createGroup(double queueId) {
            JsonObject object = new JsonObject();
            object.addProperty("queueId", queueId);
            lcds.call(uuid, mode, "createGroupV3", object);
        }

        public void declineCandidate(double slotId) {
            JsonObject object = new JsonObject();
            object.addProperty("slotId", slotId);
            lcds.call(uuid, mode, "declineCandidateV2", object);
        }

        public void retrieveFeatureToggles() {
            lcds.call(uuid, mode, "retrieveFeatureToggles", new JsonObject());
        }

        public void pickChampion(double champId) {
            JsonObject object = new JsonObject();
            object.addProperty("championId", champId);
            lcds.call(uuid, mode, "pickChampionV2", object);
        }

        public void specifyAdvertisedRole(double slot, double role) {
            JsonObject object = new JsonObject();
            object.addProperty("slotId", slot);
            object.addProperty("role", role);
            lcds.call(uuid, mode, "specifyAdvertisedRoleV1", object);
        }

        public void specifyPosition(double slot, double position) {
            JsonObject object = new JsonObject();
            object.addProperty("slotId", slot);
            object.addProperty("position", position);
            lcds.call(uuid, mode, "specifyPositionV2", object);
        }

        public void specifyRole(double slot, double role) {
            JsonObject object = new JsonObject();
            object.addProperty("slotId", slot);
            object.addProperty("role", role);
            lcds.call(uuid, mode, "specifyRoleV2", object);
        }

        public void startGroupBuildingPhase() {
            lcds.call(uuid, mode, "startGroupBuildingPhaseV1", new JsonObject());
        }

        public void startMatchmakingPhase() {
            lcds.call(uuid, mode, "startMatchmakingPhaseV1", new JsonObject());
        }

        public void retrievePlayerInfo(double queueId) {
            JsonObject object = new JsonObject();
            object.addProperty("queueId", queueId);
            lcds.call(uuid, mode, "retrievePlayerInfoV3", new JsonObject());
        }

        public void startSoloSpecPhase() {
            lcds.call(uuid, mode, "startSoloSpecPhaseV2", new JsonObject());
        }
    }
}

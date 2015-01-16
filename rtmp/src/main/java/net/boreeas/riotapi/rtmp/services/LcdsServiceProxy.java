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
 * Group finder management
 */
@AllArgsConstructor
public class LcdsServiceProxy {
    public static final String SERVICE = "lcdsServiceProxy";
    private RtmpClient client;

    /**
     * Create a group finder lobby. Might be deprecated?
     * @param type unknown - The queue type?
     * @param uuid A unique id for the team
     * @return The lobby status of the group finder lobby
     */
    public LobbyStatus createGroupFinderLobby(int type, String uuid) {
        return client.sendRpcAndWait(SERVICE, "createGroupFinderLobby", type, uuid);
    }

    /**
     * Call a group finder action
     * @param uuid The uuid of the team
     * @param mode The game mode of lobby
     * @param procCall The name of the action
     * @param object Call args
     * @return unknown
     */
    public Object call(String uuid, GameMode mode, String procCall, JsonObject object) {
        return client.sendRpcAndWait(SERVICE, "call", uuid, mode.name(), procCall, object.toString());
    }

    /**
     * Create a handle for the current lobby which wraps {@link #call(String, net.boreeas.riotapi.com.riotgames.platform.game.GameMode, String, com.google.gson.JsonObject)}-calls
     * @param uuid The uuid of the team
     * @param mode The game mode of the lobby
     * @return The created handle
     */
    public LcdsHandle createHandle(String uuid, GameMode mode) {
        return new LcdsHandle(uuid, mode, this);
    }


    @AllArgsConstructor
    public static class LcdsHandle {
        private String uuid;
        private GameMode mode;
        private LcdsServiceProxy lcds;
        private final Gson gson = new Gson();

        /**
         * Accept a player
         * @param slotId The id of the slot where the player should be accepted
         */
        public void acceptCandidate(int slotId) {
            JsonObject object = new JsonObject();
            object.addProperty("slotId", slotId);
            lcds.call(uuid, mode, "acceptCandidateV2", object);
        }

        /**
         * Create a group
         * @param queueId The queue for the group
         */
        public void createGroup(int queueId) {
            JsonObject object = new JsonObject();
            object.addProperty("queueId", queueId);
            lcds.call(uuid, mode, "createGroupV3", object);
        }

        /**
         * Decline a player
         * @param slotId The id of the slot where the player should be declined
         */
        public void declineCandidate(double slotId) {
            JsonObject object = new JsonObject();
            object.addProperty("slotId", slotId);
            lcds.call(uuid, mode, "declineCandidateV2", object);
        }

        /**
         * Retrieve enabled features?
         */
        public void retrieveFeatureToggles() {
            lcds.call(uuid, mode, "retrieveFeatureToggles", new JsonObject());
        }

        /**
         * Pick a champion
         * @param champId The id of the champion
         */
        public void pickChampion(long champId) {
            JsonObject object = new JsonObject();
            object.addProperty("championId", champId);
            lcds.call(uuid, mode, "pickChampionV2", object);
        }

        /**
         * Specify the role searched for in the target slot. Alternatively, specify own role and position?
         * @param slot The slot where the search runs / Alternatively, the position?
         * @param role The role for which there should be searched
         */
        public void specifyAdvertisedRole(long slot, long role) {
            JsonObject object = new JsonObject();
            object.addProperty("slotId", slot);
            object.addProperty("role", role);
            lcds.call(uuid, mode, "specifyAdvertisedRoleV1", object);
        }

        /**
         * Specify the position searched for in the target slot
         * @param slot The slot where the search runs
         * @param position The position for which there should be searched
         */
        public void specifyPosition(long slot, long position) {
            JsonObject object = new JsonObject();
            object.addProperty("slotId", slot);
            object.addProperty("position", position);
            lcds.call(uuid, mode, "specifyPositionV2", object);
        }

        /**
         * Specifiy the role searched for in the target slot
         * @param slot The slot where the seach runs
         * @param role The role for which there should be searched
         */
        public void specifyRole(long slot, long role) {
            JsonObject object = new JsonObject();
            object.addProperty("slotId", slot);
            object.addProperty("role", role);
            lcds.call(uuid, mode, "specifyRoleV2", object);
        }

        /**
         * Start the group building phase - unknown
         */
        public void startGroupBuildingPhase() {
            lcds.call(uuid, mode, "startGroupBuildingPhaseV1", new JsonObject());
        }

        /**
         * Start the matchmaking phase - probably after everyone has picked
         */
        public void startMatchmakingPhase() {
            lcds.call(uuid, mode, "startMatchmakingPhaseV1", new JsonObject());
        }

        /**
         * Retrieve player info
         * @param queueId The id of the queue? The summoner info?
         */
        public void retrievePlayerInfo(long queueId) {
            JsonObject object = new JsonObject();
            object.addProperty("queueId", queueId);
            lcds.call(uuid, mode, "retrievePlayerInfoV3", new JsonObject());
        }

        /**
         * Start the solo spec phase - probably where you pick your own role and position and start searching?
         */
        public void startSoloSpecPhase() {
            lcds.call(uuid, mode, "startSoloSpecPhaseV2", new JsonObject());
        }
    }
}

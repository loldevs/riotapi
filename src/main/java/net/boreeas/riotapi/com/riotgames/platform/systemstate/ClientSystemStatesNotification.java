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

package net.boreeas.riotapi.com.riotgames.platform.systemstate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Delegate;
import lombok.Getter;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 7/19/2014.
 */
@Serialization(name = "com.riotgames.platform.systemstate.ClientSystemStatesNotification", externalizable = true)
public class ClientSystemStatesNotification implements Externalizable {

    @Delegate private Inner inner;
    @Getter private JsonObject json;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(json.toString());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.json = new JsonParser().parse(in.readUTF()).getAsJsonObject();
        this.inner = new Gson().fromJson(json, Inner.class);

    }

    @Getter
    private class Inner {

        private boolean championTradeThroughLCDS;
        private boolean practiceGameEnabled;
        private boolean advancedTutorialEnabled;
        private List<Integer> practiceGameTypeConfigIdList = new ArrayList<>();
        private int minNumPlayersForPracticeGame;
        private List<Integer> freeToPlayChampionIdList = new ArrayList<>();
        // TODO inspect
        private List<Object> inactiveChampionIdList = new ArrayList<>();
        private List<Integer> inactiveSpellIdList = new ArrayList<>();
        private List<Integer> inactiveTutorialSpellIdList = new ArrayList<>();
        private List<Integer> inactiveClassicSpellIdList = new ArrayList<>();
        private List<Integer> inactiveOdinSpellIdList = new ArrayList<>();
        private List<Integer> inactiveAramSpellIdList = new ArrayList<>();
        private List<Integer> enabledQueueIdsList = new ArrayList<>();
        private List<Integer> unobtainableChampionSkinIdList = new ArrayList<>();
        private List<Integer> freeToPlayChampionForNewPlayersIdList;
        // TODO inspect
        private Map<String, Object> gameModeToInactiveSpellIds = new HashMap<>();
        private boolean archivedStatsEnabled;
        // TODO inspect
        private Map<String, Object> queueThrottleDTO = new HashMap<>();
        // TODO inspect
        private List<Map<String, Object>> gameMapEnabledDTOList = new ArrayList<>();
        private boolean storeCustomerEnabled;
        private boolean socialIntegrationEnabled;
        private boolean runeUniquePerSpellBook;
        private boolean tribunalEnabled;
        private boolean observerModeEnabled;
        private int currentSeason;
        private int freeToPlayChampionsForNewPlayersMaxLevel;
        private int spectatorSlotLimit;
        private int clientHeartBeatRateSeconds;
        private List<String> observableGameModes = new ArrayList<>();
        private String observableCustomGameModes;
        private boolean teamServiceEnabled;
        private boolean leagueServiceEnabled;
        private boolean modularGameModeEnabled;
        private double riotDataServiceDataSendProbability;
        private boolean displayPromoGamesPlayedEnabled;
        private boolean masteryPageOnServer;
        private boolean maxMasteryPagesOnServer;
        private boolean tournamentSendStatsEnabled;
        private String replayServiceAddress;
        private boolean kudosEnabled;
        private boolean buddyNotesEnabled;
        private boolean localeSpecificChatRoomsEnabled;
        // TODO inspect
        private Map<String, Object> replaySystemStates = new HashMap<>();
        private boolean sendFeedbackEventsEnabled;
        private List<String> knownGeographicGameServerRegions = new ArrayList<>();
        private boolean leaguesDecayMessagingEnabled;
    }
}

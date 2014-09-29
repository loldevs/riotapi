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

package net.boreeas.riotapi.com.riotgames.platform.clientfacade.domain;

import lombok.Data;
import net.boreeas.riotapi.com.riotgames.kudos.dto.PendingKudos;
import net.boreeas.riotapi.com.riotgames.platform.broadcast.BroadcastNotification;
import net.boreeas.riotapi.com.riotgames.platform.game.GameTypeConfig;
import net.boreeas.riotapi.com.riotgames.platform.game.PlatformGameLifecycle;
import net.boreeas.riotapi.com.riotgames.platform.statistics.PlayerStatSummaries;
import net.boreeas.riotapi.com.riotgames.platform.summoner.AllSummonerData;
import net.boreeas.riotapi.com.riotgames.platform.summoner.SummonerCatalog;
import net.boreeas.riotapi.com.riotgames.platform.systemstate.ClientSystemStatesNotification;
import net.boreeas.riotapi.rtmp.serialization.Serialization;
import net.boreeas.riotapi.rtmp.serialization.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/18/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.clientfacade.domain.LoginDataPacket")
public class LoginDataPacket {
    private PlayerStatSummaries playerStatSummaries;
    private int restrictedChatGamesRemaining;
    private int minutesUntilShutdown;
    private boolean minor;
    private int maxPracticeGameSize;
    private SummonerCatalog summonerCatalog;
    private long ipBalance;
    // TODO inspect
    private Object recconectInfo;
    private List<String> languages;
    // TODO inspect
    private List<Object> simpleMessages = new ArrayList<>();
    private AllSummonerData allSummonerData;
    private int customMinutesLeftToday;
    private int coOpVsAiMinutesLeftToday;
    // TODO inspect
    private PlatformGameLifecycle platformGameLifecycleDTO;
    // TODO inspect
    private Object bingeData;
    private boolean inGhostGame;
    private int leaverPenaltyLevel;
    private boolean bingePreventionSystemEnabledForClient;
    private int pendingBadges;
    private BroadcastNotification broadcastNotification;
    private int minutesUntilMidnight;
    private long timeUntilFirstWinOfDay;
    private long coOpVsAiMsecsUntilReset;
    private ClientSystemStatesNotification clientSystemStates;
    private long bingeMinutesRemaining;
    private PendingKudos pendingKudosDTO;
    private long leaverBusterPenaltyTime;
    private String platformId;
    private boolean matchMakingEnabled;
    private boolean minutesUntilShutdownEnabled;
    private long rpBalance;
    private List<GameTypeConfig> gameTypeConfigs = new ArrayList<>();
    @SerializedName(name = "bingeIsPlayerInBingePreventionWindow")
    private boolean isPlayerInBingePreventionWindow;
    private boolean minorShutdownEnforced;
    private String competitiveRegion;
    private long customMsecsUntilReset;
    private Object reconnectInfo;
    private Object emailStatus;
    private boolean showEmailVerificationPopup;
    private long restrictedGamesRemainingForRanked;
}

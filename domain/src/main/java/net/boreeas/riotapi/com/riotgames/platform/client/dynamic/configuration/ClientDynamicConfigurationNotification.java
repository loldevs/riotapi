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

package net.boreeas.riotapi.com.riotgames.platform.client.dynamic.configuration;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import lombok.*;
import net.boreeas.riotapi.constants.LeagueTier;
import net.boreeas.riotapi.com.riotgames.platform.game.QueueType;
import net.boreeas.riotapi.com.riotgames.platform.game.GameMode;
import net.boreeas.riotapi.rtmp.serialization.JsonSerialization;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 8/6/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.client.dynamic.configuration.ClientDynamicConfigurationNotification")
public class ClientDynamicConfigurationNotification {
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @JsonSerialization
    private ConfigOptions configs;

    private boolean delta;

    @Getter
    private class ConfigOptions {
        @SerializedName("PlatformShutdown")
        private PlatformShutdown platformShutdown;
        @SerializedName("ContextualEducation")
        private ContextualEducation contextualEducation;
        @SerializedName("DisabledChampionSkins")
        @Delegate @Getter(AccessLevel.NONE) private DisabledChampionSkins disabledChampionSkins;
        @SerializedName("BotConfigurations")
        private BotConfigurations botConfigurations;
        @SerializedName("ShareMatchHistory")
        private ShareMatchHistory shareMatchHistory;
        @SerializedName("Chat")
        private Chat chat;
        @SerializedName("GameInvites")
        private GameInvites gameInvites;
        @SerializedName("GuestSlots")
        @Delegate @Getter(AccessLevel.NONE) private GuestSlots guestSlots;
        @SerializedName("ContextualEducationURLs")
        private Map<String, String> contextualEducationURLs = new HashMap<>();
        @SerializedName("ServiceStatusAPI")
        @Delegate @Getter(AccessLevel.NONE) private ServiceStatusApi serviceStatusApi;
        @SerializedName("NewPlayerRouter")
        private NewPlayerRouter newPlayerRouter;
        @SerializedName("GameTimerSync")
        private GameTimerSync gameTimerSync;
        @SerializedName("QueueRestriction")
        private QueueRestriction queueRestriction;
        @SerializedName("DockedPrompt")
        @Delegate @Getter(AccessLevel.NONE) private DockedPrompt dockedPrompt;
        @SerializedName("QueueImages")
        private QueueImages queueImages;
        @SerializedName("SuggestedPlayers")
        private SuggestedPlayers suggestedPlayers;
        @SerializedName("SeasonRewards")
        private SeasonRewards seasonRewards;
        @SerializedName("DisabledChampions")
        @Getter(AccessLevel.NONE) private Map<QueueType, String> _disabledChampCache = new EnumMap<>(QueueType.class);
        private Map<QueueType, List<Integer>> disabledChampions = new EnumMap<>(QueueType.class);
        @SerializedName("ChampionTradeService")
        @Delegate @Getter(AccessLevel.NONE) private ChampionTradeService championTradeService;
        @SerializedName("Mutators")
        private Mutators mutators;
        @SerializedName("ChampionSelect")
        private ChampionSelect championSelect;
        @SerializedName("FeaturedGame")
        @Delegate @Getter(AccessLevel.NONE) private FeaturedGame featuredGame;
        @SerializedName("SkinRentals")
        @Delegate @Getter(AccessLevel.NONE) private SkinRentals skinRentals;

        public Map<QueueType, List<Integer>> getDisabledChampions() {
            if (disabledChampions.isEmpty()) {
                synchronized (this) {
                    if (disabledChampions.isEmpty()) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Integer>>(){}.getType();
                        for (Map.Entry<QueueType, String> entry: _disabledChampCache.entrySet()) {
                            disabledChampions.put(entry.getKey(), gson.fromJson(entry.getValue(), type));
                        }
                    }
                }
            }

            return disabledChampions;
        }
    }

    @Getter
    public class SuggestedPlayers {
        @SerializedName("HonoredPlayersLimit")
        private int honoredPlayersLimit;
        @SerializedName("FriendsOfFriendsLimit")
        private int friendsOfFriendsLimit;
        @SerializedName("Enabled")
        private boolean enabled;
        @SerializedName("OnlineFriendsLimit")
        private int onlineFriendsLimit;
        @SerializedName("PreviousPremadesLimit")
        private int previousPremadeLimits;
        @SerializedName("MaxNumSuggestedPlayers")
        private int maxNumSuggestedPlayers;
        @SerializedName("VictoriousComradesLimit")
        private int victoriousComradesLimit;
        @SerializedName("FriendsOfFriendsEnabled")
        private boolean friendsOfFriendsEnabled;
        @SerializedName("MaxNumReplacements")
        private int maxNumReplacements;
    }

    @Getter
    public class QueueImages {
        @SerializedName("OverrideQueueImage")
        private boolean overrideQueueImage;
    }

    @Getter
    public class SkinRentals {
        @SerializedName("Enabled")
        private String skinRentalStatus;
    }

    @Getter
    public class FeaturedGame {
        @SerializedName("MetadataEnabled")
        private boolean featuredGameMetadataEnabled;
    }

    @Getter
    public class ChampionSelect {
        @SerializedName("UseOptimizedChampSelectProcessor")
        private boolean useOptimizedChampSelectProcessor;
        @SerializedName("UseOptimizedSpellSelectProcessor")
        private boolean useOptimizedSpellSelectProcessor;
        @SerializedName("UseOptimizedBotChampSelectProcessor")
        private boolean useOptimizedBotChampSelectProcessor;
        @SerializedName("CollatorChampionFilterEnabled")
        private boolean collatorChampionFilterEnabled;
    }

    public class Mutators {
        @SerializedName("EnabledMutators")
        private String _mutatorCache;
        private List<String> enabledMutators;
        @SerializedName("EnabledModes")
        private String _modeCache;
        private List<GameMode> enabledModes;

        public List<String> getEnabledMutators() {
            if (enabledMutators == null) {
                synchronized (this) {
                    if (enabledMutators == null) {
                        enabledMutators = new Gson().fromJson(_mutatorCache, new TypeToken<List<String>>(){}.getType());
                    }
                }
            }

            return enabledMutators;
        }

        public List<GameMode> getEnabledModes() {
            if (enabledModes == null) {
                synchronized (this) {
                    if (enabledModes == null) {
                        enabledModes = new Gson().fromJson(_modeCache, new TypeToken<List<GameMode>>(){}.getType());
                    }
                }
            }

            return enabledModes;
        }
    }


    @Getter
    public class ChampionTradeService {
        @SerializedName("Enabled")
        private boolean championTradeServiceEnabled;
    }


    @Getter
    public class ContextualEducation {
        @SerializedName("TargetMinionsPerWave")
        private double targetMinionsPerWave;
        @SerializedName("Enabled")
        private boolean enabled;
        @SerializedName("MaxTargetSummonerLevel")
        private int maxTargetSummonerLevel;
    }

    public class DisabledChampionSkins {
        @SerializedName("DisabledChampionSkins")
        private String _disabledCache;
        private List<Integer> disabledChampionSkins;

        public List<Integer> getDisabledChampionSkins() {
            if (disabledChampionSkins == null) {
                synchronized (this) {
                    if (disabledChampionSkins == null) {
                        disabledChampionSkins = new Gson().fromJson(_disabledCache, new TypeToken<List<Integer>>(){}.getType());
                    }
                }
            }

            return disabledChampionSkins;
        }
    }

    @Getter
    public class BotConfigurations {
        @SerializedName("IntermediateInCustoms")
        private boolean intermediateInCustoms;
    }

    @Getter
    public class ShareMatchHistory {
        @SerializedName("MatchHistoryUrlTemplate")
        private String matchHistoryUrlTemplate;
        @SerializedName("AdvancedGameDetailsUrlTemplate")
        private String advancedGameDetaulsUrlTemplate;
        @SerializedName("MatchDetailsUrlTemplate")
        private String matchDetailsUrlTemplate;
        @SerializedName("ShareEndOfGameEnabled")
        private boolean shareEndOfGameEnabled;
        @SerializedName("ShareGameUrlTemplate")
        private String shareGameUrlTemplate;
        @SerializedName("MatchHistoryEnabled")
        private boolean matchHistoryEnabled;
    }

    @Getter
    public class Chat {
        @SerializedName("Rename_general_group_throttle")
        private double renameGeneralGroupThrottle;
        @SerializedName("Default_public_chat_rooms")
        private String defaultPublicChatRooms;
    }

    @Getter
    public class GameInvites {
        @SerializedName("ServiceEnabled")
        private boolean serviceEnabled;
        @SerializedName("GameInviteServiceEnabled")
        private boolean gameInviteServiceEnabled;
        @SerializedName("InviteBulkMaxSize")
        private int inviteBulkMaxSize;
        @SerializedName("LobbyCreationEnabled")
        private boolean lobbyCreationEnabled;
    }

    @Getter
    public class GuestSlots {
        @SerializedName("Enabled")
        private boolean guestSlotsEnabled;
    }

    @Getter
    public class ServiceStatusApi {
        @SerializedName("Enabled")
        private boolean serviceStatusApiEnabled;
    }

    @Getter
    public class NewPlayerRouter {
        @SerializedName("QueueId")
        private int queueId;
        @SerializedName("ABDisablingOfTutorial")
        private boolean abDisablingOfTutorial;
    }

    @Getter
    public class GameTimerSync {
        @SerializedName("Enabled")
        private boolean enabled;
        @SerializedName("PercentOfTotalTimerToSyncAt")
        private double percentOfTotalTimerToSyncAt;
    }

    @Getter
    public class QueueRestriction {
        @SerializedName("ServiceEnabled")
        private boolean enabled;
        @SerializedName("RankedDuoQueueRestrictionMode")
        private String restrictionMode;
        @SerializedName("RankedDuoQueueRestrictionMaxDelta")
        private int restrictionMaxDelta;
        @SerializedName("RankedDuoQueueDefaultUnseededTier")
        private LeagueTier defaultUnseededTier;
    }

    @Getter
    public class DockedPrompt {
        @SerializedName("EnabledNewDockedPromptRenderer")
        private boolean newDockedPromptRendererEnabled;
    }

    @Getter
    public class SeasonRewards {
        @SerializedName("Maximum_team_reward_level")
        private int maximumTeamRewardLevel;
        @SerializedName("Enabled")
        private boolean enabled;
        @SerializedName("ServiceCallEnabled")
        private boolean serviceCallEnabled;
        @SerializedName("Minimum_points_per_reward_level")
        @Getter(AccessLevel.NONE) private String _minPointsPerLevel;
        private List<Integer> minimumPointsPerRewardLevel;
        @SerializedName("Minimum_win_team_reward_level_1")
        private int level1MinimumTeamWins;
        @SerializedName("Minimum_win_team_reward_level_2")
        private int level2MinimumTeamWins;
        @SerializedName("Minimum_win_team_reward_level_3")
        private int level3MinimumTeamWins;

        public List<Integer> getMinimumPointsPerRewardLevel() {
            if (minimumPointsPerRewardLevel == null) {
                synchronized (this) {
                    if (minimumPointsPerRewardLevel == null) {
                        minimumPointsPerRewardLevel = Arrays.asList(_minPointsPerLevel.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList());
                    }
                }
            }

            return minimumPointsPerRewardLevel;
        }
    }

    @Getter
    public class PlatformShutdown {
        @SerializedName("Enabled")
        private boolean enabled;
    }
}

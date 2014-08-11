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

package net.boreeas.riotapi.com.riotgames.platform.statistics;

import lombok.Data;
import net.boreeas.riotapi.com.riotgames.platform.game.GameMode;
import net.boreeas.riotapi.com.riotgames.platform.game.GameType;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 7/19/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.statistics.PlayerGameStats")
public class PlayerGameStats {
    // TODO inspect
    private Object skinName;
    private boolean ranked;
    private int skinIndex;
    private List<FellowPlayerInfo> fellowPlayers = new ArrayList<>();
    private String gameType;
    private long experienceEarned;
    // TODO inspect
    private Object rawStatsJson;
    private boolean eligibleFirstWinOfDay;
    // TODO inspect
    private Object difficulty;
    private int gameMapId;
    private boolean leaver;
    private long spell1;
    private long spell2;
    private String gameTypeEnum;
    private long teamId;
    private long summonerId;
    private List<RawStat> statistics = new ArrayList<>();
    private boolean afk;
    // TODO inspect
    private Object id;
    private long boostXpEarned;
    private long level;
    private boolean invalid;
    private long userId;
    private Date createDate;
    private int userServerPing;
    private int adjustedRating;
    private int premadeSize;
    private long boostIpEarned;
    private long gameId;
    private int timeInQueue;
    private long ipEarned;
    private int eloChange;
    private String gameMode;
    private String difficultyString;
    private long kCoefficient;
    private int teamRating;
    private String subType;
    private String queueType;
    private boolean premadeTeam;
    private long predictedWinPct;
    private long rating;
    private long championId;

    public GameType getGameType() {
        return GameType.getByName(gameType);
    }

    public GameMode getGameMode() {
        return GameMode.getByName(gameMode);
    }
}

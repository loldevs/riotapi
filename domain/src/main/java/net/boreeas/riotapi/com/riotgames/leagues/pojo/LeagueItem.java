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

package net.boreeas.riotapi.com.riotgames.leagues.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.boreeas.riotapi.constants.LeagueTier;
import net.boreeas.riotapi.com.riotgames.platform.game.QueueType;
import net.boreeas.riotapi.rtmp.serialization.Serialization;
import net.boreeas.riotapi.rtmp.serialization.SerializedName;

/**
 * Created on 4/14/2014.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Serialization(name = "com.riotgames.leagues.pojo.LeagueItemDTO")
public class LeagueItem {
    @SerializedName(name = "freshBlood")
    private boolean isFreshBlood;
    @SerializedName(name = "hotStreak")
    private boolean isHotStreak;
    @SerializedName(name = "inactive")
    private boolean isInactive;
    @SerializedName(name = "veteran")
    private boolean isVeteran;
    private long lastPlayed;
    private String leagueName;
    private int leaguePoints;
    private MiniSeries miniSeries;
    private String playerOrTeamId;
    private String playerOrTeamName;
    private String queueType;
    private String rank;
    private String tier;
    private int wins;

    // Not transmitted via net.boreeas.riotapi.rest
    private int losses;
    private long timeUntilDecay;
    private long timeLastDecayMessageShown;
    private int previousDayLeaguePosition;
    private boolean displayDecayWarning;
    private int demotionWarning;

    public int getRank() {
        switch (rank) {
            case "I": return 1;
            case "II": return 2;
            case "III": return 3;
            case "IV": return 4;
            case "V": return 5;
            default: throw new IllegalStateException("Invalid rank: " + rank);
        }
    }

    public LeagueTier getTier() {
        return LeagueTier.getByName(tier);
    }

    public QueueType getQueueType() {
        return QueueType.getByName(queueType);
    }
}

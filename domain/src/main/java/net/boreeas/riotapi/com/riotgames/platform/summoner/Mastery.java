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

package net.boreeas.riotapi.com.riotgames.platform.summoner;

import lombok.EqualsAndHashCode;
import lombok.Data;
import net.boreeas.riotapi.rtmp.serialization.Serialization;
import net.boreeas.riotapi.rtmp.serialization.SerializedName;

/**
 * Created on 7/18/2014.
 */
@Data
@EqualsAndHashCode(of = {"id"})
@Serialization(name = "com.riotgames.platform.summoner.Talent")
public class Mastery {
    private int index;
    private String level1Desc;
    private String level3Desc;
    private String level2Desc;
    private String level4Desc;
    private String level5Desc;
    private int minLevel;
    private int maxRank;

    @SerializedName(name = "tltId")
    private int id;
    @SerializedName(name = "talentGroupId")
    private int groupId;

    private int gameCode;
    private int minTier;

    // TODO inspect
    @SerializedName(name = "prereqTalentGameCode")
    private Object prereqMasteryGameCode;

    private String name;
    @SerializedName(name = "talentRowId")
    private int masteryRowId;
}

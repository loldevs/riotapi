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

package net.boreeas.riotapi.com.riotgames.platform.catalog.champion;

import lombok.Data;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 7/29/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.catalog.champion.ChampionDTO")
public class Champion {
    private double purchased;
    private List<ChampionSkin> championSkins = new ArrayList<>();
    private boolean rankedPlayEnabled;
    private Date purchaseDate;
    private int winCountRemaining;
    private boolean botEnabled;
    private boolean active;
    private Date endDate;
    private boolean freeToPlay;
    private int championId;
    private boolean freeToPlayReward;
    private boolean owned;
}

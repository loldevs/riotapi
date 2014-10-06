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

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Serialization(name = "com.riotgames.leagues.pojo.LeagueListDTO")
public class LeagueList {
    private List<LeagueItem> entries = new ArrayList<>();
    private String name;
    private String queue;
    private String tier;
    private long maxLeagueSize;
    private long nextApexUpdate;

    // Not transmitted via RTMP
    private String participantId;

    // Not transmitted via net.boreeas.riotapi.rest
    private String requestorsRank;
    private String requestorsName;

    public QueueType getQueue() {
        return QueueType.getByName(queue);
    }

    public LeagueTier getTier() {
        return LeagueTier.getByName(tier);
    }
}

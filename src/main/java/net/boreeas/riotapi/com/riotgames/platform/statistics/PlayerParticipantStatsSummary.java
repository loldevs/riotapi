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

package net.boreeas.riotapi.com.riotgames.platform.statistics;

import lombok.Data;
import net.boreeas.riotapi.com.riotgames.team.TeamInfo;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/20/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.statistics.PlayerParticipantStatsSummary")
public class PlayerParticipantStatsSummary {
    private String skinName;
    private double gameId;
    private int profileIconId;
    private int elo;
    private boolean leaver;
    private double leaves;
    private double teamId;
    private int eloChange;
    private List<RawStat> statistics = new ArrayList<>();
    private double level;
    private boolean botPlayer;
    private boolean isMe;
    private boolean inChat;
    private double userId;
    private double spell1Id;
    private double spell2Id;
    private double losses;
    private double wins;
    private String summonerName;
    private TeamInfo teamInfo;
    private boolean reportEnabled;
    private boolean kudosEnabled;
}

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

package net.boreeas.riotapi.com.riotgames.team.dto;

import lombok.Data;
import net.boreeas.riotapi.com.riotgames.team.TeamId;
import net.boreeas.riotapi.com.riotgames.team.stats.TeamStatSummary;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 7/20/2014.
 */
@Data
@Serialization(name = "com.riotgames.team.dto.TeamDTO")
public class Team {
    private TeamStatSummary teamStatSummary;
    private String status;
    private String tag;
    private String name;
    private Roster roster;
    // TODO inspect
    private Object messageOfDay;
    private TeamId teamId;
    private Date createDate;
    private Date modifyDate;
    private Date lastGameDate;
    private Date lastJoinDate;
    private Date secondLastJoinDate;
    private Date thirdLastJoinDate;
    private long secondsUntilEligibleForDeletion;
    private List<Object> matchHistory = new ArrayList<>();
}

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

package net.boreeas.riotapi.com.riotgames.team.stats;

import lombok.Getter;
import net.boreeas.riotapi.com.riotgames.team.TeamId;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
@Serialization(name="com.riotgames.team.stats.TeamStatSummary")
public class TeamStatSummary {
    private TeamId teamId;
    private String teamIdString;
    private List<TeamStatDetails> teamStatDetails = new ArrayList<>();
}

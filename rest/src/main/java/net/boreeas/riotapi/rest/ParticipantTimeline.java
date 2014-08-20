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

package net.boreeas.riotapi.rest;

import lombok.Getter;
import net.boreeas.riotapi.constants.Role;


/**
 * @author Malte Schütze
 */
@Getter
public class ParticipantTimeline {
    private ParticipantTimelineData ancientGolemAssistsPerMinCounts;
    private ParticipantTimelineData ancientGolemKillsPerMinCounts;
    private ParticipantTimelineData assistedLaneDeathsPerMinDeltas;
    private ParticipantTimelineData assistedLaneKillsPerMinDeltas;
    private ParticipantTimelineData baronAssistsPerMinCounts;
    private ParticipantTimelineData baronKillsPerMinCounts;
    private ParticipantTimelineData creepsPerMinDeltas;
    private ParticipantTimelineData csDiffPerMinDeltas;
    private ParticipantTimelineData damageTakenDiffPerMinDeltas;
    private ParticipantTimelineData dragonAssistsPerMinCounts;
    private ParticipantTimelineData dragonKillsPerMinCounts;
    private ParticipantTimelineData elderLizardAssistsPerMinCounts;
    private ParticipantTimelineData elderLizardKillsPerMinCounts;
    private ParticipantTimelineData goldPerMinDeltas;
    private ParticipantTimelineData inhibitorAssistsPerMinCounts;
    private ParticipantTimelineData inhibitorKillsPerMinCounts;
    private ParticipantTimelineData towerAssistsPerMinCounts;
    private ParticipantTimelineData towerKillsPerMinCounts;
    private ParticipantTimelineData towerKillsPerMinDeltas;
    private ParticipantTimelineData vilemawAssistsPerMinCounts;
    private ParticipantTimelineData vilemawKillsPerMinCounts;
    private ParticipantTimelineData wardsPerMinDeltas;
    private ParticipantTimelineData xpDiffPerMinDeltas;
    private ParticipantTimelineData xpPerMinDeltas;
    private Lane lane;
    private Role role;

    private enum Lane {
        MID,
        MIDDLE,
        TOP,
        JUNGLE,
        BOT,
        BOTTOM
    }
}

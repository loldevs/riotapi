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
import net.boreeas.riotapi.com.riotgames.platform.game.Participant;
import net.boreeas.riotapi.com.riotgames.platform.game.QueueType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A match history entry.
 * @author Malte Schütze
 */
@Getter
public class MatchDetail {
    private long mapId;
    private Date matchCreation;
    private long matchDuration;
    private long matchId;
    private String matchVersion;
    private List<ParticipantIdentity> participantIdentities = new ArrayList<>();
    private List<Participant> participants = new ArrayList<>();
    private QueueType queueType;
    private String region;
    private List<PostMatchTeamOverview> teams = new ArrayList<>();
    private MatchTimeline timeline;
}

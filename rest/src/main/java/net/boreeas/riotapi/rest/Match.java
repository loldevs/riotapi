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
import net.boreeas.riotapi.com.riotgames.platform.game.GameMode;
import net.boreeas.riotapi.com.riotgames.platform.game.GameType;
import net.boreeas.riotapi.com.riotgames.platform.game.QueueType;
import net.boreeas.riotapi.constants.Season;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Malte Schütze
 */
@Getter
public class Match {
    private long mapId;
    private Date matchCreation;
    private long matchDuration;
    private long matchId;
    private String matchVersion;
    private List<ParticipantIdentity> participantIdentities = new ArrayList<>();
    private List<Participant> participants = new ArrayList<>();
    private String region;
    private GameMode matchMode;
    private GameType matchType;
    private QueueType queueType;
    private String platformId;
    private Season season;
}

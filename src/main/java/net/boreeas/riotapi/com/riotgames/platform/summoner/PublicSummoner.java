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

package net.boreeas.riotapi.com.riotgames.platform.summoner;

import lombok.Getter;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.Date;

/**
 * Created on 7/20/2014.
 */
@Getter
@Serialization(name = "com.riotgames.platform.summoner.PublicSummoner")
public class PublicSummoner {
    private String internalName;
    private double acctId;
    private String name;
    private int profileIconId;
    private Date revisionDate;
    private double revisionId;
    private double summonerLevel;
    private double summonerId;
}

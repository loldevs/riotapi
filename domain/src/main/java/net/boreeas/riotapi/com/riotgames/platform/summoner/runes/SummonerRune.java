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

package net.boreeas.riotapi.com.riotgames.platform.summoner.runes;

import lombok.Data;
import net.boreeas.riotapi.com.riotgames.platform.catalog.runes.Rune;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.Date;

/**
 * Created on 7/20/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform..SummonerRune")
public class SummonerRune {
    private Date purchased;
    private Date purchaseDate;
    private int runeId;
    private int quantity;
    private Rune rune;
    private long summonerId;
}

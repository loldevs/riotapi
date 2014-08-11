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

/**
 * Created on 4/14/2014.
 */
@Getter
public class ChampionStats {
    private double armor;
    private double armorperlevel;
    private double attackdamage;
    private double attackdamageperlevel;
    private double attackrange;
    private double attackspeedoffset;
    private double attackspeedperlevel;
    private double crit;
    private double critperlevel;
    private double hp;
    private double hpperlevel;
    private double hpregen;
    private double hpregenperlevel;
    private double movespeed;
    private double mp;
    private double mpperlevel;
    private double mpregen;
    private double mpregenperlevel;
    private double spellblock;
    private double spellblockperlevel;
}

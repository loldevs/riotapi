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

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
public class Champion {
    private List<String> allytips;
    private String blurb;
    private List<String> enemytips;
    private int id;
    private Image image;
    private ChampionInfo info;
    private String key;
    private String lore;
    private String partype;
    private ChampionPassive passive;
    private List<RecommendedItems> recommended = new ArrayList<>();
    private List<Skin> skins = new ArrayList<>();
    private List<Spell> spells = new ArrayList<>();
    private ChampionStats stats;
    private List<String> tags = new ArrayList<>();
    private String title;
}

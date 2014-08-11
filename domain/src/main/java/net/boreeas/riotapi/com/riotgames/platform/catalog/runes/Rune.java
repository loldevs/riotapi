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

package net.boreeas.riotapi.com.riotgames.platform.catalog.runes;

import lombok.EqualsAndHashCode;
import lombok.Data;
import net.boreeas.riotapi.com.riotgames.platform.catalog.ItemEffect;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/20/2014.
 */
@Data
@EqualsAndHashCode(of = {"itemId", "tier"})
@Serialization(name = "com.riotgames.platform.catalog.runes.Rune")
public class Rune {
    // TODO inspect
    private Object imagePath;
    // TODO inspect
    private Object toolTip;
    private int tier;
    private int itemId;
    private RuneType runeType;
    private int duration;
    private int gameCode;
    private List<ItemEffect> itemEffects = new ArrayList<>();
    private String baseType;
    private String description;
    private String name;
    // TODO inspect
    private Object uses;
}

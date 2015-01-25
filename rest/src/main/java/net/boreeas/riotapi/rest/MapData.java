/*
 * Copyright 2015 The LolDevs team (https://github.com/loldevs)
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

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic map information
 * @author Malte Schütze
 */
@Data
public class MapData {
    /**
     * The id of the map
     */
    private int mapId;
    /**
     * The items which can't get bought on this map
     */
    private List<Integer> unpurchasableItemList = new ArrayList<>();
    /**
     * Image data for the map
     */
    private Image image;
    /**
     * The name of the map
     */
    private String mapName;
}

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
import net.boreeas.riotapi.constants.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Malte Schütze
 */
@Getter
public class Event {
    private List<Integer> assistingParticipantIds = new ArrayList<>();
    private BuildingType buildingType;
    private int creatorId;
    private int killerId;
    private int teamId;
    private int victimId;
    private Type eventType;
    private Lane laneType;
    private MonsterType monsterType;
    private Point position;
    private long timestamp;
    private TowerType towerType;
    private WardType wardType;

    public enum Type {
        BUILDING_KILL,
        CHAMPION_KILL,
        ELITE_MONSTER_KILL,
        WARD_KILL,
        WARD_PLACED,
        ITEM_DESTROYED,
        ITEM_PURCHASED,
        ITEM_SOLD,
        ITEM_UNDO,
        SKILL_LEVEL_UP
    }
}

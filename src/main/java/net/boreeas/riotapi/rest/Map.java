/*
 * Copyright 2014 Malte Schütze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi.rest;

/**
 * Created on 4/12/2014.
 */
public enum Map {
    SR_SUMMER(1, "Summoner's Rift"),
    SR_AUTUMN(2, "Summoner's Rift"),
    PROVING_GROUNDS(3, "The Proving Grounds"),
    TWISTED_TREELINE_ORIG(4, "Twisted Treeline (Original)"),
    CRYSTAL_SCAR(8, "The Crystal Scar"),
    TWISTED_TREELINE_CURR(10, "Twisted Treeline (Current)"),
    HOWLING_ABYSS(12, "Howling Abyss");


    public final int id;
    public final String name;

    private Map(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Map getById(int id) {
        switch (id) {
            case 1: return SR_SUMMER;
            case 2: return SR_AUTUMN;
            case 3: return PROVING_GROUNDS;
            case 4: return TWISTED_TREELINE_ORIG;
            case 8: return CRYSTAL_SCAR;
            case 10: return TWISTED_TREELINE_CURR;
            case 12: return HOWLING_ABYSS;
            default: return null;
        }
    }
}

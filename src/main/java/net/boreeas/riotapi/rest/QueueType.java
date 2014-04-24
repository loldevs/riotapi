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
public enum QueueType {
    NONE("NONE"),
    NORMAL_5v5("NORMAL"),
    NORMAL_3v3("NORMAL_3x3"),
    DOMINION("ODIN_UNRANKED"),
    ARAM("ARAM_UNRANKED_5x5"),
    BOT("BOT"),
    BOT_3v3("BOT_3x3"),
    RANKED_SOLO_5v5("RANKED_SOLO_5x5"),
    RANKED_TEAM_3v3("RANKED_TEAM_3x3"),
    RANKED_TEAM_5v5("RANKED_TEAM_5x5"),
    ONE_FOR_ALL("ONEFORALL_5x5"),
    FIRSTBLOOD_1v1("FIRSTBLOOD_1x1"),
    FIRSTBLOOD_2v2("FIRSTBLOOD_2x2"),
    HEXAKILL("SR_6x6"),
    TEAMBUILDER("CAP_5x5"),
    URF("URF"),
    URF_BOT("URF_BOT");


    public final String name;

    private QueueType(String name) {
        this.name = name;
    }

    public static QueueType getByName(String name) {
        for (QueueType type: values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }

        return null;
    }
}

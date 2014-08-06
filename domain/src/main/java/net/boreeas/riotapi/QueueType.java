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

package net.boreeas.riotapi;

/**
 * Created on 4/12/2014.
 */
public enum QueueType {
    ARAM,
    ARAM_UNRANKED_5x5,
    BOT,
    BOT_3x3,
    CAP_5x5,
    CLASSIC,
    FIRSTBLOOD,
    FIRSTBLOOD_1x1,
    FIRSTBLOOD_2x2,
    NIGHTMARE_BOT,
    NONE,
    NORMAL,
    NORMAL_3x3,
    ODIN,
    ODIN_UNRANKED,
    ONEFORALL,
    ONEFORALL_5x5,
    RANKED_PREMADE_3x3,
    RANKED_PREMADE_5x5,
    RANKED_SOLO_5x5,
    RANKED_TEAM_3x3,
    RANKED_TEAM_5x5,
    SR_6x6,
    TUTORIAL,
    URF,
    URF_BOT;


    public static QueueType getByName(String name) {
        for (QueueType type: values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }

        return null;
    }
}

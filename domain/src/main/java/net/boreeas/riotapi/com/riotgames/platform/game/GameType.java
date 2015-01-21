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

package net.boreeas.riotapi.com.riotgames.platform.game;

import net.boreeas.riotapi.rtmp.serialization.Serialization;

/**
 * Created on 4/12/2014.
 */
@Serialization(name = "com.riotgames.platform.game.GameType")
public enum GameType {
    CUSTOM_GAME,
    TUTORIAL_GAME,
    MATCHED_GAME,
    COOP_VS_AI_GAME,
    RANKED_GAME,
    NORMAL_GAME,
    PRACTICE_GAME;

    public static GameType getByName(String name) {
        for (GameType type: values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }

        return null;
    }
}

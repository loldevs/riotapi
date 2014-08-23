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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.boreeas.riotapi.com.riotgames.platform.catalog.champion.Champion;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

/**
 * @author Malte Schütze
 */
@Getter
@Setter
@ToString
@Serialization(name = "com.riotgames.platform.game.BotParticipant")
public class BotParticipant extends GameParticipant {
    public enum SkillLevel {
        TUTORIAL(4),
        EASY(0),
        MEDIUM(1),
        INTRO(5),
        HARD(2),
        UBER(3);

        public final int id;

        private SkillLevel(int id) {
            this.id = id;
        }

        public static SkillLevel byId(int id) {
            for (SkillLevel value: values()) {
                if (value.id == id) {
                    return value;
                }
            }

            return null;
        }
    }

    private Champion champion;
    private String botSkillLevelName;
    private String teamId;
    private int botSkillLevel;

    public SkillLevel getBotSkillLevel() {
        return SkillLevel.byId(botSkillLevel);
    }
}

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
import net.boreeas.riotapi.rtmp.serialization.Serialization;

/**
 * @author Malte Schütze
 */
@Getter
@Setter
@ToString
@Serialization(name = "com.riotgames.platform.game.GameParticipant")
public class GameParticipant implements Participant {
    public enum Team {
        FRIENDLY,
        ENEMY;

        public static Team byId(int id) {
            switch (id) {
                case 1:
                    return FRIENDLY;
                case 2:
                    return ENEMY;
                default:
                    return null;
            }
        }
    }

    public enum PickMode {
        NOT_PICKING(0),
        DONE(1),
        ACTIVE(2);

        public final int id;

        private PickMode(int id) {
            this.id = id;
        }

        public static PickMode byId(int id) {
            for (PickMode value: values()) {
                if (value.id == id) {
                    return value;
                }
            }

            return null;
        }
    }

    private int badges;
    private String summonerName;
    private boolean isGameOwner;
    private int team;
    private boolean isMe;
    private String teamName;
    private int pickTurn;
    private int pickMode;
    private String summonerInternalName;

    public Team getTeam() {
        return Team.byId(team);
    }

    public PickMode getPickMode() {
        return PickMode.byId(pickMode);
    }
}

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

package net.boreeas.riotapi.constants;

/**
 * @author Malte Schütze
 */
public enum PingType {
    DEFAULT(0xb0),
    DANGER(0xb2),
    ENEMY_MISSING(0xb3),
    ON_MY_WAY(0xb4),
    RETREAT(0xb5),
    ASSIST_ME(0xb6);

    public final int spectatorId;

    private PingType(int spectatorId) {
        this.spectatorId = spectatorId;
    }

    public static PingType getBySpectatorId(int id) {
        for (PingType ping: values()) {
            if (ping.spectatorId == id) {
                return ping;
            }
        }

        throw new IllegalArgumentException("Unknown ping type " + id);
    }
}

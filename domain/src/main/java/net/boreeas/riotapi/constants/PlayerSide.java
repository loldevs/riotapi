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
 * Created on 4/14/2014.
 */
public enum PlayerSide {
    BLUE(100, 0),
    PURPLE(200, 1);

    public final int id;
    public final int spectatorId;

    private PlayerSide(int id, int spectatorId) {
        this.id = id;
        this.spectatorId = spectatorId;
    }

    public static PlayerSide getById(int id) {
        switch (id) {
            case 100: return BLUE;
            case 200: return PURPLE;
            default: return null;
        }
    }

    public static PlayerSide getBySpectatorId(int id) {
        for (PlayerSide side: values()) {
            if (side.spectatorId == id) {
                return side;
            }
        }

        throw new IllegalArgumentException("Unknown side " + id);
    }
}

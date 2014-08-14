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

package net.boreeas.riotapi.spectator;

/**
 * Represents a supported spectator platform
 * Created on 4/28/2014.
 */
public enum Platform {
    EUW1,
    EUN1,
    NA1,
    OC1,
    BR1,
    LA1,
    LA2,
    TR,
    PBE;

    public static final Platform byName(String name) {
        for (Platform platform: values()) {
            if (platform.name().equals(name)) {
                return platform;
            }
        }
        return null;
    }
}

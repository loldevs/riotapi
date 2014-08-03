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

package net.boreeas.riotapi.spectator;

/**
 * Represents a supported spectator platform
 * Created on 4/28/2014.
 */
public enum Platform {
    EUW("EUW1"),
    EUNE("EUN1"),
    NA("NA1"),
    OCE("OC1"),
    BR("BR1"),
    LA1("LA1"),
    LA2("LA2"),
    TR("TR"),
    PBE("PBE1");

    public final String name;

    private Platform(String name) {
        this.name = name;
    }

    public static final Platform byName(String name) {
        for (Platform platform: values()) {
            if (platform.name.equals(name)) {
                return platform;
            }
        }
        return null;
    }
}

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

package net.boreeas.riotapi.rest;

/**
 * Created on 4/14/2014.
 */
public enum SpellData {
    ALL,
    COOLDOWN,
    COOLDOWN_BURN("cooldownBurn"),
    COST,
    COST_BURN("costBurn"),
    COST_TYPE("costType"),
    EFFECT,
    EFFECT_BURN("effectBurn"),
    IMAGE,
    KEY,
    LEVELTIP,
    MAXRANK,
    MODES,
    RANGE,
    RANGE_BURN("rangeBurn"),
    RESOURCE,
    SANITIZED_DESCRIPTION("sanitizedDescription"),
    SANITIZED_TOOLTIP("sanitizedTooltip"),
    TOOLTIP,
    VARS;

    public final String name;

    private SpellData() {
        this.name = name().toLowerCase();
    }

    private SpellData(String name) {
        this.name = name;
    }

    public static SpellData getByName(String name) {
        for (SpellData data: values()) {
            if (data.name.equals(name)) {
                return data;
            }
        }

        return null;
    }
}

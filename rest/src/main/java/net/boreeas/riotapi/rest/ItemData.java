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
public enum ItemData {
    ALL,
    COLLOQUIAL("colloq"),
    CONSUME_ON_FULL("consumeOnFull"),
    CONSUMED,
    DEPTH,
    FROM,
    GOLD,
    GROUPS,
    HIDE_FROM_ALL("hideFromAll"),
    IMAGE,
    IN_STORE("inStore"),
    INTO,
    MAPS,
    REQUIRED_CHAMPIONS("requiredChampions"),
    SANITIZED_DESCRIPTION("sanitizedDescription"),
    SPECIAL_RECIPE("specialRecipe"),
    STACKS,
    STATS,
    TAGS,
    TREE;

    public final String name;

    private ItemData() {
        this.name = name().toLowerCase();
    }

    private ItemData(String name) {
        this.name = name;
    }

    public static ItemData getByName(String name) {
        for (ItemData data: values()) {
            if (data.name.equals(name)) {
                return data;
            }
        }

        return null;
    }
}

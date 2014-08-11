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

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 4/14/2014.
 */
public class Realm {
    @Getter private String cdn;
    private String css;
    private String dd;
    private String l;
    private String lg;
    private Map<String, String> n = new HashMap<>();
    @Getter private int profileiconmax;
    @Getter private String store;
    private String v;

    public String getCssVersion() {
        return css;
    }

    public String getDragonVersion() {
        return dd;
    }

    public String getDefaultLanguage() {
        return l;
    }

    public String getLegacyScriptMode() {
        return lg;
    }

    public Map<String, String> getVersions() {
        return n;
    }

    public String getVersion() {
        return v;
    }
}

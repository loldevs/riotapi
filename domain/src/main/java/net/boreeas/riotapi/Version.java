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

package net.boreeas.riotapi;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Malte Schütze
 */
@EqualsAndHashCode(of = {"versionString"})
public class Version implements Comparable<Version> {
    private List<Integer> versionParts = new ArrayList<>();
    @Getter private String versionString;

    public Version(String version) {
        this.versionString = version;

        for (String part: version.split("\\.")) {
            versionParts.add(Integer.parseInt(part));
        }
    }


    @Override
    public int compareTo(Version other) {
        int max = Integer.max(versionParts.size(), other.versionParts.size());
        for (int i = 0; i < max; i++) {
            if (i >= versionParts.size()) {
                return -1;
            } else if (i >= other.versionParts.size()) {
                return 1;
            } else if (versionParts.get(i) < other.versionParts.get(i)) {
                return -1;
            } else if (versionParts.get(i) > other.versionParts.get(i)) {
                return 1;
            }
        }

        return 0;
    }
}

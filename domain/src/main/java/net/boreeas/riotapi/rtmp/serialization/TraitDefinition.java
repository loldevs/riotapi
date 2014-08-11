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

package net.boreeas.riotapi.rtmp.serialization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 5/26/2014.
 */
@Getter
@RequiredArgsConstructor
@ToString
public class TraitDefinition {
    private final String name;
    private final boolean dynamic;
    private final boolean externalizable;
    private final List<FieldRef> staticFields = new ArrayList<>();
    private final List<FieldRef> dynamicFields = new ArrayList<>();

    public int getHeader() {
        int header = staticFields.size() << 4;
        header |= (dynamic ? 1 : 0) << 3;
        header |= (externalizable ? 1 : 0) << 2;
        header |= 0b10;    // inline trait def
        header |= 0b01;    // inline object

        return header;
    }
}

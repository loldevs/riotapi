/*
 * Copyright 2014 Malte Schütze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi.rtmp.amf;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines an object that may be sent over an AMF stream
 * Created on 4/16/2014.
 */
@Getter
public class TraitDefinition {
    private String type;
    private boolean externalizable;
    private boolean dynamic;
    private List<String> members = new ArrayList<>();

    public TraitDefinition(String type, boolean externalizable, boolean dynamic) {
        this.type = type;
        this.externalizable = externalizable;
        this.dynamic = dynamic;
    }

    public void addMember(String name) {
        members.add(name);
    }

    public List<String> getMembers() {
        return Collections.unmodifiableList(members);
    }
}

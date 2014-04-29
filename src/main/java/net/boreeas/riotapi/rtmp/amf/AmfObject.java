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

import lombok.Delegate;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an object that may be transmitted over an AMF stream
 * Created on 4/16/2014.
 */
public class AmfObject {
    @Delegate(excludes = TraitAddMemberExclude.class)
    private TraitDefinition traitDef;
    @Delegate(excludes = TypedObjectGetTypeExclude.class)
    private Map<String, TypedObject> fields = new HashMap<>();

    public AmfObject(TraitDefinition traitDef) {
        this.traitDef = traitDef;
    }

    public void setField(String key, TypedObject value) {
        if (!getMembers().contains(key) && !(isDynamic() | isExternalizable())) {
            throw new IllegalArgumentException("Can't add fields to non-dynamic non-externalizable types");
        }

        fields.put(key, value);
    }

    public void setField(String key, Object value) {
        setField(key, TypedObject.fromObject(value));
    }

    public TypedObject getField(String key) {
        return fields.get(key);
    }



    private interface TraitAddMemberExclude {
        public void addMember(String name);
    }

    private interface TypedObjectGetTypeExclude {
        public DataType getType();
    }
}

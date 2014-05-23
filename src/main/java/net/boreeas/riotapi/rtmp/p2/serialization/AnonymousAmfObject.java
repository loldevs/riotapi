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

package net.boreeas.riotapi.rtmp.p2.serialization;

import lombok.AllArgsConstructor;
import lombok.ToString;
import net.boreeas.riotapi.rtmp.p2.serialization.amf0.Amf0ObjectSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf0.Amf0Type;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.DynamicObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 5/14/2014.
 */
@AllArgsConstructor
@ToString
@SerializationContext(traitName = "", dynamic = true, serializerAmf0 = AnonymousAmfObject.AmfObjectAmf0Serializer.class)
public class AnonymousAmfObject implements DynamicObject {
    private final Map<String, Object> fields;

    public AnonymousAmfObject() {
        this(new HashMap<>());
    }

    public Object get(String field) {
        return fields.get(field);
    }

    public void set(String field, Object data) {
        fields.put(field, data);
    }

    public boolean hasField(String field) {
        return fields.containsKey(field);
    }

    public Map<String, Object> getDynamicMembers() {
        return fields;
    }


    public static class AmfObjectAmf0Serializer extends Amf0ObjectSerializer {
        @Override
        public void serialize(Object obj, DataOutputStream out) throws IOException {
            AnonymousAmfObject amfObj = (AnonymousAmfObject) obj;

            for (Map.Entry<String, Object> entry: amfObj.fields.entrySet()) {
                out.writeUTF(entry.getKey());
                writer.encodeAmf0(entry.getValue());
            }

            out.writeShort(0);
            out.write(Amf0Type.OBJECT_END.ordinal());
        }
    }
}

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

import lombok.AllArgsConstructor;
import lombok.ToString;
import net.boreeas.riotapi.rtmp.serialization.amf0.Amf0ObjectDeserializer;
import net.boreeas.riotapi.rtmp.serialization.amf0.Amf0ObjectSerializer;
import net.boreeas.riotapi.rtmp.serialization.amf0.Amf0Type;
import net.boreeas.riotapi.rtmp.serialization.amf3.Amf3ObjectDeserializer;
import net.boreeas.riotapi.rtmp.serialization.amf3.Amf3ObjectSerializer;
import net.boreeas.riotapi.rtmp.serialization.amf3.DynamicObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 5/14/2014.
 */
@AllArgsConstructor
@ToString
@Serialization(dynamic = true,
               amf0Serializer = AnonymousAmfObject.AmfObjectAmf0Serializer.class,
               amf3Serializer = AnonymousAmfObject.AmfObjectAmf3Serializer.class,
               amf0Deserializer = AnonymousAmfObject.AmfObjectAmf0Deserializer.class,
               amf3Deserializer = AnonymousAmfObject.AmfObjectAmf3Deserializer.class)
public class AnonymousAmfObject implements DynamicObject {
    private final Map<String, Object> fields;

    public AnonymousAmfObject() {
        this(new HashMap<>());
    }

    public Object get(String field) {
        return fields.get(field);
    }

    public void put(String field, Object data) {
        fields.put(field, data);
    }

    public boolean hasField(String field) {
        return fields.containsKey(field);
    }

    @Override
    public Map<String, Object> getFields() {
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

    public static class AmfObjectAmf3Serializer extends Amf3ObjectSerializer {
        @Override
        protected TraitDefinition getCachedTraitDef(Object o) {
            return traitDefCache.get(o);
        }

        @Override
        protected TraitDefinition getTraitDefiniton(Object o) {
            TraitDefinition def = new TraitDefinition("", true, false);
            for (String key: ((AnonymousAmfObject) o).getFields().keySet()) {
                def.getDynamicFields().add(new FieldRef(key, key, o.getClass()));
            }

            return def;
        }

        @Override
        protected void cacheTraitDef(Object o, TraitDefinition def) {
            traitDefCache.put(o, def);
        }

        @Override
        protected Object getDynamicField(Object o, FieldRef ref) throws NoSuchFieldException, IllegalAccessException {
            return ((AnonymousAmfObject) o).get(ref.getName());
        }
    }

    public static class AmfObjectAmf0Deserializer extends Amf0ObjectDeserializer {
        @Override
        protected void setField(Object object, String name, Object value) throws NoSuchFieldException, IllegalAccessException {
            ((AnonymousAmfObject) object).put(name, value);
        }
    }

    public static class AmfObjectAmf3Deserializer extends Amf3ObjectDeserializer {
        @Override
        protected void setDynamicField(Object target, Object value, FieldRef ref) throws NoSuchFieldException, IllegalAccessException {
            ((AnonymousAmfObject) target).put(ref.getSerializedName(), value);
        }
    }
}

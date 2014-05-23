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

import lombok.ToString;
import net.boreeas.riotapi.rtmp.amf.TraitDefinition;
import net.boreeas.riotapi.rtmp.p2.serialization.amf0.Amf0ObjectSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf0.Amf0Type;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3ObjectSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.DynamicObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created on 5/14/2014.
 */
@ToString
@SerializationContext(traitName = "", dynamic = true, serializerAmf0 = TypedAmfObject.AmfObjectAmf0Serializer.class,
        serializerAmf3 = TypedAmfObject.AmfObjectAmf3Serializer.class)
public class TypedAmfObject extends AnonymousAmfObject implements DynamicObject {
    private final String type;

    public TypedAmfObject(String type) {
        this.type = type;
    }


    public static class AmfObjectAmf0Serializer extends Amf0ObjectSerializer {
        @Override
        public void serialize(Object obj, DataOutputStream out) throws IOException {
            TypedAmfObject amfObj = (TypedAmfObject) obj;

            out.writeUTF(amfObj.type);

            for (Map.Entry<String, Object> entry: amfObj.getDynamicMembers().entrySet()) {
                out.writeUTF(entry.getKey());
                writer.encodeAmf0(entry.getValue());
            }

            out.writeShort(0);
            out.write(Amf0Type.OBJECT_END.ordinal());
        }
    }

    public static class AmfObjectAmf3Serializer extends Amf3ObjectSerializer {
        public void serialize(Object obj, DataOutputStream out) throws IOException {
            TypedAmfObject object = (TypedAmfObject) obj;
            TraitDefinition def = new TraitDefinition(object.type, false, true);

            if (traitRefTable.containsKey(def)) {
                serializeTraitRef(traitRefTable.get(def));
            } else {
                traitRefTable.put(def, traitRefTable.size());
                serializeTraitDefHeader(def);
            }

            serializeDynamicMembers(object.getDynamicMembers());
        }
    }
}

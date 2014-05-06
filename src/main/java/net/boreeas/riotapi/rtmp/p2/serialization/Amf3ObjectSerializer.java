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

import lombok.Setter;
import net.boreeas.riotapi.rtmp.amf.TraitDefinition;
import net.boreeas.riotapi.rtmp.p2.AmfWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created on 5/5/2014.
 */
public class Amf3ObjectSerializer implements AmfSerializer {

    @Setter private Map<TraitDefinition, Integer> traitRefTable;
    @Setter private AmfWriter writer;

    @Override
    public void serialize(Object o, OutputStream out) throws IOException {
        SerializationContext context = o.getClass().getAnnotation(SerializationContext.class);

        TraitDefinition def = new TraitDefinition(context.traitName(), context.externalizable(), context.dynamic());

        if (traitRefTable.containsKey(def)) {
            writer.serializeAmf3(traitRefTable.get(def) << 1);
            def = traitRefTable.entrySet().stream().filter((entry) -> entry.getKey().equals(def)).findFirst().get().getKey();
        } else {
            serializeTraitDef(def, o);
        }
    }

    private void serializeTraitDef(TraitDefinition def, Object o) {
        traitRefTable.put(def, traitRefTable.size());

        SerializationContext context = o.getClass().getAnnotation(SerializationContext.class);
        if (context.members().length == 0) {
            Set<String> excludes = new HashSet<>();
            for (String s: context.excludes()) {excludes.add(s);}

            for (Field f: o.getClass().getDeclaredFields()) {
                if (!excludes.contains(f.getName())) {
                    def.addMember(name);
                }
            }
        }

        int header = def.getMembers().size();
    }
}

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

package net.boreeas.riotapi.rtmp.serialization.amf3;

import lombok.Setter;
import lombok.SneakyThrows;
import net.boreeas.riotapi.rtmp.serialization.AmfSerializer;
import net.boreeas.riotapi.rtmp.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.serialization.FieldRef;
import net.boreeas.riotapi.rtmp.serialization.NoSerialization;
import net.boreeas.riotapi.rtmp.serialization.Serialization;
import net.boreeas.riotapi.rtmp.serialization.SerializedName;
import net.boreeas.riotapi.rtmp.serialization.TraitDefinition;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created on 5/5/2014.
 */
public class Amf3ObjectSerializer implements AmfSerializer {

    @Setter protected Map<TraitDefinition, Integer> traitRefTable;
    @Setter protected Map<Object, TraitDefinition> traitDefCache;
    @Setter protected AmfWriter writer;

    @Override
    @SneakyThrows({NoSuchFieldException.class, IllegalAccessException.class})
    public void serialize(Object o, DataOutputStream out) throws IOException {

        TraitDefinition traitDef;
        if ((traitDef = getCachedTraitDef(o)) != null) {
            writer.serializeAmf3(traitRefTable.get(traitDef) << 2 | 1);
        } else {
            traitDef = getTraitDefiniton(o);
            cacheTraitDef(o, traitDef);
            traitRefTable.put(traitDef, traitRefTable.size());


            writer.serializeAmf3(traitDef.getHeader());
            writer.serializeAmf3(traitDef.getName());
            for (FieldRef field: traitDef.getStaticFields()) {
                writer.serializeAmf3(field.getSerializedName());
            }
        }

        for (FieldRef ref: traitDef.getStaticFields()) {
            writer.encodeAmf3(getStaticField(o, ref));
        }

        if (traitDef.isDynamic()) {
            for (FieldRef ref: traitDef.getDynamicFields()) {
                writer.serializeAmf3(ref.getSerializedName());
                writer.encodeAmf3(getDynamicField(o, ref));
            }
            writer.serializeAmf3("");
        }
    }

    protected Object getStaticField(Object o, FieldRef ref) throws NoSuchFieldException, IllegalAccessException {
        Field f = ref.getLocation().getDeclaredField(ref.getName());
        f.setAccessible(true);
        return f.get(o);
    }

    protected Object getDynamicField(Object o, FieldRef ref) throws NoSuchFieldException, IllegalAccessException {
        Field f = ref.getLocation().getDeclaredField(ref.getName());
        f.setAccessible(true);
        return f.get(o);
    }

    protected TraitDefinition getTraitDefiniton(Object o) {
        Serialization serialization = o.getClass().getAnnotation(Serialization.class);
        boolean dynamic = (serialization == null) ? false : serialization.dynamic();
        boolean externalizable = (serialization == null) ? false : serialization.externalizable();
        String name = (serialization == null) ? "" : serialization.name();

        TraitDefinition traitDef = new TraitDefinition(name, dynamic, externalizable);
        Class c = o.getClass();

        while (c != null) {
            for (Field f: c.getDeclaredFields()) {
                if (f.isAnnotationPresent(NoSerialization.class)) continue;
                if (Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) continue;

                String fieldName = (f.isAnnotationPresent(SerializedName.class)) ? f.getAnnotation(SerializedName.class).name() : f.getName();
                FieldRef fieldRef = new FieldRef(f.getName(), fieldName, c);

                if (f.isAnnotationPresent(Dynamic.class)) {
                    traitDef.getDynamicFields().add(fieldRef);
                } else {
                    traitDef.getStaticFields().add(fieldRef);
                }
            }

            c = c.getSuperclass();
        }

        return traitDef;
    }

    protected TraitDefinition getCachedTraitDef(Object o) {
        return traitDefCache.get(o.getClass());
    }

    protected void cacheTraitDef(Object o, TraitDefinition def) {
        traitDefCache.put(o.getClass(), def);
    }
}

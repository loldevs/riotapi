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

package net.boreeas.riotapi.rtmp.serialization.amf3;

import com.google.gson.Gson;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.rtmp.serialization.*;

import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created on 5/5/2014.
 */
@Log4j
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


        if (o instanceof Externalizable) {
            ((Externalizable) o).writeExternal(writer);
            return;
        } else if (traitDef.isExternalizable()) {
            log.warn("Externalizable object " + traitDef.getName() + " does not implement externalizable");
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
        return f.isAnnotationPresent(JsonSerialization.class) ? new Gson().toJson(f.get(o)) : f.get(o);
    }

    protected Object getDynamicField(Object o, FieldRef ref) throws NoSuchFieldException, IllegalAccessException {
        Field f = ref.getLocation().getDeclaredField(ref.getName());
        f.setAccessible(true);
        return f.isAnnotationPresent(JsonSerialization.class) ? new Gson().toJson(f.get(o)) : f.get(o);
    }

    protected TraitDefinition getTraitDefiniton(Object o) {
        Serialization serialization = o.getClass().getAnnotation(Serialization.class);
        boolean dynamic = (serialization != null) && serialization.dynamic();
        boolean externalizable = (serialization != null) && serialization.externalizable();
        String name = (serialization == null) ? "" : serialization.name();

        TraitDefinition traitDef = new TraitDefinition(name, dynamic, externalizable);
        Class c = o.getClass();

        if (!externalizable) {
            while (c != null) {
                for (Field f : c.getDeclaredFields()) {
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

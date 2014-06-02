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
import net.boreeas.riotapi.rtmp.serialization.AmfReader;
import net.boreeas.riotapi.rtmp.serialization.FieldRef;
import net.boreeas.riotapi.rtmp.serialization.TraitDefinition;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created on 5/18/2014.
 */
public class Amf3ObjectDeserializer {
    @Setter private Class cls;

    @SneakyThrows({InstantiationException.class, IllegalAccessException.class, NoSuchFieldException.class})
    public Object deserialize(AmfReader reader, TraitDefinition def) throws IOException {

        Object instance = cls.newInstance();

        for (FieldRef ref: def.getStaticFields()) {
            setStaticField(instance, reader.decodeAmf3(), ref);
        }

        if (def.isDynamic()) {
            String name;
            while (!(name = reader.readUTF()).isEmpty()) {
                setDynamicField(instance, reader.decodeAmf3(), new FieldRef(name, name, cls));
            }
        }

        return instance;
    }
    protected void setDynamicField(Object target, Object value, FieldRef ref) throws NoSuchFieldException, IllegalAccessException {
        Field field = ref.getLocation().getDeclaredField(ref.getName());
        field.setAccessible(true);
        field.set(target, value);
    }

    protected void setStaticField(Object target, Object value, FieldRef ref) throws NoSuchFieldException, IllegalAccessException {
        Field field = ref.getLocation().getDeclaredField(ref.getName());
        field.setAccessible(true);
        field.set(target, value);
    }
}

/*
 * Copyright 2014 Malte Schütze
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

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.rtmp.TypeConverter;
import net.boreeas.riotapi.rtmp.serialization.AmfReader;
import net.boreeas.riotapi.rtmp.serialization.FieldRef;
import net.boreeas.riotapi.rtmp.serialization.TraitDefinition;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created on 5/18/2014.
 */
@Log4j
public class Amf3ObjectDeserializer {
    @Setter protected Class cls;
    @Setter protected List<Object> objectRefTable;

    @SneakyThrows({InstantiationException.class, IllegalAccessException.class, NoSuchFieldException.class, ClassNotFoundException.class})
    public Object deserialize(AmfReader reader, TraitDefinition def) throws IOException {

        Object instance = cls.newInstance();
        objectRefTable.add(instance);

        if (instance instanceof Externalizable) {
            ((Externalizable) instance).readExternal(new ObjectInputStream(reader));
            return instance;
        }

        for (FieldRef ref: def.getStaticFields()) {
            Object obj = reader.decodeAmf3();
            setStaticField(instance, obj, ref);
        }

        if (def.isDynamic()) {
            String name;
            while (!(name = reader.readAmf3String()).isEmpty()) {
                setDynamicField(instance, reader.decodeAmf3(), new FieldRef(name, name, cls));
            }
        }

        return instance;
    }

    protected void setDynamicField(Object target, Object value, FieldRef ref) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field field = ref.getLocation().getDeclaredField(ref.getName());
        field.setAccessible(true);
        field.set(target, TypeConverter.typecast(field.getType(), value));
    }

    protected void setStaticField(Object target, Object value, FieldRef ref) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        if (ref.getName() == null) {
            log.warn("Skipping field with no match: " + ref.getSerializedName() + " = " + value);
            return;
        }
        Field field = ref.getLocation().getDeclaredField(ref.getName());
        field.setAccessible(true);
        field.set(target, TypeConverter.typecast(field.getType(), value));
    }


}

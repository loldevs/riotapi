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

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.rtmp.serialization.*;

import java.io.Externalizable;
import java.io.IOException;
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
            ((Externalizable) instance).readExternal(reader);
            return instance;
        } else if (def.isExternalizable()) {
            log.warn("Externalizable object " + def.getName() + " does not implement Externalizable");
        }

        for (FieldRef ref: def.getStaticFields()) {
            try {
                Object obj = reader.decodeAmf3();
                setStaticField(instance, obj, ref);
            } catch (IOException ex) {
                throw new IOException(ref + ": Error during deserialization", ex);
            }
        }

        if (def.isDynamic()) {
            String name;
            while (!(name = reader.readAmf3String()).isEmpty()) {
                try {
                    setDynamicField(instance, reader.decodeAmf3(), new FieldRef(name, name, cls));
                } catch (IOException ex) {
                    throw new IOException("Field" + name + " of " + cls + ": Error during deserialization", ex);
                }
            }
        }

        return instance;
    }

    protected void setDynamicField(Object target, Object value, FieldRef ref) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field field = ref.getLocation().getDeclaredField(ref.getName());
        field.setAccessible(true);
        try {
            field.set(target, TypeConverter.typecast(field.getType(), value, field.isAnnotationPresent(JsonSerialization.class)));
        } catch (IllegalArgumentException ex) {
            throw new SerializationException(ref + " (value=" + value + "): " + ex.getMessage());
        }
    }

    protected void setStaticField(Object target, Object value, FieldRef ref) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        if (ref.getName() == null) {
            if (ref.getSerializedName().equals("dataVersion") || ref.getSerializedName().equals("futureData")) {
                log.trace("Skipping field with no match: " + ref.getSerializedName() + " = " + value);
            } else {
                log.warn("Skipping field with no match: " + ref.getSerializedName() + " = " + value);
            }
            return;
        }
        Field field = ref.getLocation().getDeclaredField(ref.getName());
        field.setAccessible(true);
        try {
            field.set(target, TypeConverter.typecast(field.getType(), value, field.isAnnotationPresent(JsonSerialization.class)));
        } catch (IllegalArgumentException ex) {
            throw new SerializationException(ref + " (value=" + value + "): " + ex.getMessage());
        }
    }


}

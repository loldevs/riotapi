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

package net.boreeas.riotapi.rtmp.serialization.amf0;

import lombok.Setter;
import lombok.SneakyThrows;
import net.boreeas.riotapi.rtmp.serialization.*;

import java.io.Externalizable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created on 5/27/2014.
 */
public class Amf0ObjectDeserializer {
    @Setter private Class<?> cls;

    @SneakyThrows
    public Object deserialize(AmfReader reader) {
        Object result = cls.newInstance();

        if (result instanceof Externalizable) {
            ((Externalizable) result).readExternal(reader);
            return result;
        }


        for (Map.Entry<String, Object> field : reader.readAmf0KeyValuePairs().entrySet()) {
            setField(result, field.getKey(), field.getValue());
        }

        return result;
    }

    protected void setField(Object object, String name, Object value) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Class c = object.getClass();

        while (c != null) {

            for (Field field: c.getDeclaredFields()) {
                if (isTargetField(field, name)) {

                    field.setAccessible(true);
                    try {
                        field.set(object, TypeConverter.typecast(field.getType(), value, field.isAnnotationPresent(JsonSerialization.class)));
                    } catch (IllegalArgumentException ex) {
                        throw new SerializationException("Field " + name + " of " + c + " (value=" + value + "): " + ex.getMessage());
                    }
                    return;
                }
            }

            c = c.getSuperclass();
        }

        throw new NoSuchFieldException();
    }


    protected boolean isTargetField(Field field, String name) {
        return isSerializedNameEqual(field, name) || isNameEqualAndFieldSerializable(field, name);
    }

    protected boolean isSerializedNameEqual(Field field, String name) {
        return field.isAnnotationPresent(SerializedName.class) && field.getAnnotation(SerializedName.class).name().equals(name);
    }

    protected boolean isNameEqualAndFieldSerializable(Field field, String name) {
        return field.getName().equals(name) && !field.isAnnotationPresent(NoSerialization.class) &&
                !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers());
    }
}

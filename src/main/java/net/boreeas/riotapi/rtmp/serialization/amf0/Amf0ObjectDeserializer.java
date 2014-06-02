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

package net.boreeas.riotapi.rtmp.serialization.amf0;

import lombok.Setter;
import lombok.SneakyThrows;
import net.boreeas.riotapi.rtmp.serialization.AmfReader;
import net.boreeas.riotapi.rtmp.serialization.NoSerialization;
import net.boreeas.riotapi.rtmp.serialization.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created on 5/27/2014.
 */
public class Amf0ObjectDeserializer {
    @Setter private Class<?> cls;

    @SneakyThrows
    public Object deserialize(AmfReader reader) {
        Object result = cls.newInstance();
        for (Map.Entry<String, Object> field: reader.readAmf0KeyValuePairs().entrySet()) {
            setField(result, field.getKey(), field.getValue());
        }

        return result;
    }

    protected Object typecast(Type t, Object value) {

        // amf0 can only encode doubles
        if (t == Long.class || t == long.class) {
            return ((Double) value).longValue();
        } else if (t == Integer.class || t == int.class) {
            return ((Double) value).intValue();
        } else if (t == Short.class || t == short.class) {
            return ((Double) value).shortValue();
        } else if (t == Byte.class || t == byte.class) {
            return ((Double) value).byteValue();
        } else if (t == Float.class || t == float.class) {
            return ((Double) value).floatValue();
        } else {
            return value;
        }
    }

    protected void setField(Object object, String name, Object value) throws NoSuchFieldException, IllegalAccessException {
        Class c = object.getClass();

        while (c != null) {

            for (Field field: c.getDeclaredFields()) {
                if (isTargetField(field, name)) {

                    field.setAccessible(true);
                    field.set(object, typecast(field.getType(), value));
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

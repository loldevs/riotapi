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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 5/18/2014.
 */
public class Amf3ObjectDeserializer {
    @Setter protected Class cls;
    @Setter protected List<Object> objectRefTable;

    @SneakyThrows({InstantiationException.class, IllegalAccessException.class, NoSuchFieldException.class})
    public Object deserialize(AmfReader reader, TraitDefinition def) throws IOException {

        Object instance = cls.newInstance();
        objectRefTable.add(instance);

        for (FieldRef ref: def.getStaticFields()) {
            Object obj = reader.decodeAmf3();
            setStaticField(instance, obj, ref);
        }

        if (def.isDynamic()) {
            String name;
            while (!(name = reader.readUTF()).isEmpty()) {
                setDynamicField(instance, reader.decodeAmf3(), new FieldRef(name, name, cls));
            }
        }

        return instance;
    }

    protected void setDynamicField(Object target, Object value, FieldRef ref) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field field = ref.getLocation().getDeclaredField(ref.getName());
        field.setAccessible(true);
        field.set(target, typecast(field.getType(), value));
    }

    protected void setStaticField(Object target, Object value, FieldRef ref) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field field = ref.getLocation().getDeclaredField(ref.getName());
        field.setAccessible(true);
        field.set(target, typecast(field.getType(), value));
    }

    /**
     * There may be type mismatched caused by the serialization process which we try to fix here
     * @param cls The target field type
     * @param obj The object which is to be assigned to the field
     * @param <T>
     * @return An object assignable to fields with that type, or the original object if no type conversion exists
     */
    protected <T> Object typecast(Class<T> cls, Object obj) throws InstantiationException, IllegalAccessException {
        if (cls.isArray()) {
            Class<?> inner = cls.getComponentType();
            Object arr;

            if (obj.getClass().isArray()) {
                arr = arrayToArray(inner, obj);
            } else if (obj instanceof List) {
                arr = listToArray(inner, (List) obj);
            } else if (obj instanceof Map) {
                arr = listToArray(inner, mapToList(ArrayList.class, (Map) obj));
            } else {
                throw new IllegalArgumentException("Unknown conversion " + obj.getClass() + " => " + cls);
            }

            return arr;
        }

        if (List.class.isAssignableFrom(cls)) {
            if (obj.getClass().isArray()) {
                return arrayToList((Class<? extends List>) cls, obj);
            } else if (obj instanceof List) {
                List l = (List) cls.newInstance();
                l.addAll((java.util.Collection) obj);
                return l;
            } else if (obj instanceof Map) {
                return mapToList((Class<? extends List>) cls, (Map<?, ?>) obj);
            } else {
                throw new IllegalArgumentException("Unknown conversion " + obj.getClass() + " => " + cls);
            }
        }

        if (Map.class.isAssignableFrom(cls)) {
            if (obj.getClass().isArray()) {
                return listToMap((Class<? extends Map>) cls, arrayToList(ArrayList.class, obj));
            } else if (obj instanceof Map) {
                Map map = (Map) cls.newInstance();
                map.putAll((Map) obj);
                return map;
            } else if (obj instanceof List) {
                return listToMap((Class<? extends Map>) cls, (List) obj);
            } else {
                throw new IllegalArgumentException("Unknown conversion " + obj.getClass() + " => " + cls);
            }
        }

        return obj;
    }

    private Object arrayToArray(Class<?> componentType, Object original) {
        if (componentType.equals(original.getClass().getComponentType()));

        int len = Array.getLength(original);
        Object arr = Array.newInstance(componentType, len);
        for (int i = 0; i < len; i++) {
            Array.set(arr, i, componentType.cast(Array.get(original, i)));
        }

        return arr;
    }

    private List arrayToList(Class<? extends List> listCls, Object original) throws IllegalAccessException, InstantiationException {
        List list = listCls.newInstance();
        int len = Array.getLength(original);
        for (int i = 0; i < len; i++) {
            list.add(Array.get(original, i));
        }

        return list;
    }

    private Object listToArray(Class<?> componentType, List list) {
        Object arr = Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(arr, i, componentType.cast(list.get(i)));
        }

        return arr;
    }

    private Map listToMap(Class<? extends Map> mapClass, List list) throws IllegalAccessException, InstantiationException {
        Map map = mapClass.newInstance();
        for (int i = 0; i < list.size(); i++) {
            map.put(i, list.get(i));
        }

        return map;
    }

    private List mapToList(Class<? extends List> cls, Map<?, ?> map) throws IllegalAccessException, InstantiationException {
        List list = cls.newInstance();
        for (Map.Entry entry: map.entrySet()) {
            Object key = entry.getKey();
            // Filter integer keys from map
            if (key instanceof Integer || key instanceof Short || key instanceof Byte) {
                int i = (int) key;

                // keys are unordered, fill up the list first if needed
                if (list.size() <= i) {
                    for (int n = list.size(); n <= i; n++) {
                        list.add(null);
                    }
                }

                list.set(i, entry.getValue());
            }
        }

        return list;
    }
}

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

package net.boreeas.riotapi.rtmp.serialization;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.rtmp.serialization.amf3.DynamicObject;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created on 7/20/2014.
 */
@Log4j
public class TypeConverter {

    /**
     * There may be type mismatched caused by the serialization process which we try to fix here
     * @param cls The target field type
     * @param obj The object which is to be assigned to the field
     * @param json Attempt to deserialize objects as json
     * @return An object assignable to fields with that type, or the original object if no type conversion exists
     */
    public static <T> Object typecast(Class<T> cls, Object obj, boolean json) throws InstantiationException, IllegalAccessException {
        if (cls.isInstance(obj)) {
            return cls.cast(obj);
        }

        if (obj == null) {
            if (cls.isPrimitive()) {
                log.warn("Converting null to primitive");
                if (cls == boolean.class) {
                    return false;
                }
                return 0;
            }
            return obj;
        }

        if (cls.isArray()) {
            Class<?> inner = cls.getComponentType();

            if (obj.getClass().isArray()) {
                return arrayToArray(inner, obj);
            } else if (obj instanceof List) {
                return listToArray(inner, (List) obj);
            } else if (obj instanceof Map) {
                return listToArray(inner, mapToList(ArrayList.class, (Map) obj));
            }
        }

        if (List.class.isAssignableFrom(cls)) {
            if (obj.getClass().isArray()) {
                if (cls.isInterface() || (cls.getModifiers() & Modifier.ABSTRACT) > 0) {
                    return arrayToList(ArrayList.class, obj);
                } else {
                    return arrayToList((Class<? extends List>) cls, obj);
                }
            } else if (obj instanceof List) {
                List l;
                if (cls.isInterface() || (cls.getModifiers() & Modifier.ABSTRACT) > 0) {
                    l = new ArrayList<>();
                } else{
                    l = (List) cls.newInstance();
                }
                l.addAll((java.util.Collection) obj);
                return l;
            } else if (obj instanceof Map) {
                if (cls.isInterface() || (cls.getModifiers() & Modifier.ABSTRACT) > 0) {
                    return mapToList(ArrayList.class, (Map<?, ?>) obj);
                } else {
                    return mapToList((Class<? extends List>) cls, (Map<?, ?>) obj);
                }
            }
        }

        if (Map.class.isAssignableFrom(cls)) {
            if (obj.getClass().isArray()) {
                if (cls.isInterface() || (cls.getModifiers() & Modifier.ABSTRACT) > 0) {
                    return listToMap(HashMap.class, arrayToList(ArrayList.class, obj));
                } else {
                    return listToMap((Class<? extends Map>) cls, arrayToList(ArrayList.class, obj));
                }
            } else if (obj instanceof Map) {
                Map map;
                if (cls.isInterface() || (cls.getModifiers() & Modifier.ABSTRACT) > 0) {
                    map = new HashMap<>();
                } else {
                    map = (Map) cls.newInstance();
                }
                map.putAll((Map) obj);
                return map;
            } else if (obj instanceof List) {
                if (cls.isInterface() || (cls.getModifiers() & Modifier.ABSTRACT) > 0) {
                    return listToMap(HashMap.class, (List) obj);
                } else {
                    return listToMap((Class<? extends Map>) cls, (List) obj);
                }
            } else if (obj instanceof DynamicObject) {
                if (cls.isInterface() || (cls.getModifiers() & Modifier.ABSTRACT) > 0) {
                    return dynObjectToMap(HashMap.class, (DynamicObject) obj);
                } else {
                    return dynObjectToMap((Class<? extends Map>) cls, (DynamicObject) obj);
                }
            }
        }

        if (cls.isAssignableFrom(AmfObject.class)) {
            if (obj instanceof Map) {
                return mapToAmfObj((Map<?,?>) obj);
            } else if (obj instanceof DynamicObject) {
                AmfObject dynObj = new AmfObject();
                for (Map.Entry<String, Object> field: ((DynamicObject) obj).getFields().entrySet()) {
                    dynObj.set(field.getKey(), field.getValue());
                }
                return dynObj;
            }
        }

        if (cls.isAssignableFrom(AnonymousAmfObject.class)) {
            if (obj instanceof Map) {
                return mapToAnonAmfObj((Map<?,?>) obj);
            } else if (obj instanceof AmfObject) {
                AnonymousAmfObject dynObj = new AnonymousAmfObject();
                for (Map.Entry<String, Object> field: ((DynamicObject) obj).getFields().entrySet()) {
                    dynObj.put(field.getKey(), field.getValue());
                }
                return dynObj;
            }
        }


        if (obj instanceof Number) {
            if (cls == Long.class || cls == long.class) {
                return ((Number) obj).longValue();
            } else if (cls == Integer.class || cls == int.class) {
                return ((Number) obj).intValue();
            } else if (cls == Short.class || cls == short.class) {
                return ((Number) obj).shortValue();
            } else if (cls == Byte.class || cls == byte.class) {
                return ((Number) obj).byteValue();
            } else if (cls == Float.class || cls == float.class) {
                return ((Number) obj).floatValue();
            } else if (cls == Double.class || cls == double.class) {
                return ((Number) obj).doubleValue();
            } else if (cls == Date.class) {
                return new Date(((Number) obj).longValue());
            }
        }

        if (obj instanceof Boolean && cls == boolean.class) {
            return obj;
        }


        if (obj.getClass().isPrimitive()) {
            if (obj.getClass() == boolean.class ||  obj.getClass() == Boolean.class || cls == boolean.class || cls == Boolean.class) {
                throw new IllegalArgumentException("Unknown conversion " + obj.getClass() + " => " + cls);
            }

            if (cls.isPrimitive()) {
                return cls.cast(obj);
            }

            if (cls == Long.class) {
                return (long) obj;
            } else if (cls == Double.class) {
                return (double) obj;
            } else if (cls == Integer.class) {
                return (int) obj;
            } else if (cls == Byte.class) {
                return (byte) obj;
            } else if (cls == Float.class) {
                return (float) obj;
            } else if (cls == Short.class) {
                return (short) obj;
            } else if (cls == Character.class) {
                return (char) obj;
            }
        }

        if (cls.isEnum() && obj instanceof String) {
            try {
                Method method = cls.getMethod("getByName", String.class);
                T result = (T) method.invoke(null, obj);
                if (result != null) {
                    return result;
                }
            } catch (NoSuchMethodException e) {
                for (T t: cls.getEnumConstants()) {
                    if (((Enum) t).name().equals(obj)) {
                        return t;
                    }
                }
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        if (json && obj instanceof String) {
            return new Gson().fromJson((String) obj, cls);
        }

        throw new IllegalArgumentException("Unknown conversion " + obj.getClass() + " => " + cls);
    }

    public static AnonymousAmfObject mapToAnonAmfObj(Map<?, ?> obj) throws IllegalAccessException, InstantiationException {
        AnonymousAmfObject instance = new AnonymousAmfObject();
        for (Map.Entry entry: obj.entrySet()) {
            instance.put(entry.getKey().toString(), entry.getValue());
        }

        return instance;
    }

    public static AmfObject mapToAmfObj(Map<?, ?> obj) throws IllegalAccessException, InstantiationException {
        AmfObject instance = new AmfObject();
        for (Map.Entry entry: obj.entrySet()) {
            instance.set(entry.getKey().toString(), entry.getValue());
        }

        return instance;
    }

    public static Map dynObjectToMap(Class<? extends Map> mapCls, DynamicObject obj) throws IllegalAccessException, InstantiationException {
        Map map = mapCls.newInstance();
        for (Map.Entry<String, Object> field: obj.getFields().entrySet()) {
            map.put(field.getKey(), field.getValue());
        }

        return map;
    }


    public static Object arrayToArray(Class<?> componentType, Object original) {
        if (componentType.equals(original.getClass().getComponentType()));

        int len = Array.getLength(original);
        Object arr = Array.newInstance(componentType, len);
        for (int i = 0; i < len; i++) {
            Array.set(arr, i, componentType.cast(Array.get(original, i)));
        }

        return arr;
    }

    public static List arrayToList(Class<? extends List> listCls, Object original) throws IllegalAccessException, InstantiationException {
        List list = listCls.newInstance();
        int len = Array.getLength(original);
        for (int i = 0; i < len; i++) {
            list.add(Array.get(original, i));
        }

        return list;
    }

    public static Object listToArray(Class<?> componentType, List list) {
        Object arr = Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(arr, i, componentType.cast(list.get(i)));
        }

        return arr;
    }

    public static Map listToMap(Class<? extends Map> mapClass, List list) throws IllegalAccessException, InstantiationException {
        Map map = mapClass.newInstance();
        for (int i = 0; i < list.size(); i++) {
            map.put(i, list.get(i));
        }

        return map;
    }

    public static List mapToList(Class<? extends List> cls, Map<?, ?> map) throws IllegalAccessException, InstantiationException {
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

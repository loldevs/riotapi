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

package net.boreeas.riotapi.rtmp.amf;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.Map;

/**
 * Created on 4/16/2014.
 */
@Getter
@AllArgsConstructor
public class TypedObject {
    public static final TypedObject NULL = new TypedObject(DataType.NULL, null);

    private DataType type;
    private Object value;

    public int getInt() {
        return (int) value;
    }

    public double getDouble() {
        return (double) value;
    }

    public boolean getBool() {
        return (boolean) value;
    }

    public AmfObject getObject() {
        return (AmfObject) value;
    }

    public byte[] getByteArray() {
        return (byte[]) value;
    }

    public Map<Object, TypedObject> getArray() {
        return (Map<Object, TypedObject>) value;
    }

    public String getString() {
        return (String) value;
    }

    public Date getDate() {
        return (Date) value;
    }

    public Map<TypedObject, TypedObject> getDict() {
        return (Map<TypedObject, TypedObject>) value;
    }

    public int[] getIntVector() {
        return (int[]) value;
    }

    public long[] getUintVector() {
        return (long[]) value;
    }

    public double[] getDoubleVector() {
        return (double[]) value;
    }

    public TypedObject[] getObjectVector() {
        return (TypedObject[]) value;
    }

    public static TypedObject fromObject(Object obj) {
        if (obj == null) {
            return NULL;
        }

        if (obj instanceof TypedObject) {
            return (TypedObject) obj;
        }

        if (obj instanceof Integer) {
            return new TypedObject(DataType.INTEGER, obj);
        }

        if (obj instanceof Double) {
            return new TypedObject(DataType.DOUBLE, obj);
        }

        if (obj instanceof Boolean) {
            return new TypedObject((boolean) obj ? DataType.TRUE : DataType.FALSE, obj);
        }

        if (obj instanceof AmfObject) {
            return new TypedObject(DataType.OBJECT, obj);
        }

        if (obj instanceof byte[]) {
            return new TypedObject(DataType.BYTE_ARRAY, obj);
        }

        if (obj instanceof Map) {
            if (((Map) obj).isEmpty()) {
                throw new RuntimeException("Can't infer type from empty map");
            } else if (((Map) obj).keySet().iterator().next() instanceof TypedObject) {
                return new TypedObject(DataType.DICTIONARY, obj);
            } else {
                return new TypedObject(DataType.ARRAY, obj);
            }
        }

        if (obj instanceof String) {
            return new TypedObject(DataType.STRING, obj);
        }

        if (obj instanceof Date) {
            return new TypedObject(DataType.DATE, obj);
        }

        if (obj instanceof int[]) {
            return new TypedObject(DataType.VECTOR_INT, obj);
        }

        if (obj instanceof long[]) {
            return new TypedObject(DataType.VECTOR_UINT, obj);
        }

        if (obj instanceof double[]) {
            return new TypedObject(DataType.VECTOR_DOUBLE, obj);
        }

        if (obj instanceof TypedObject[]) {
            return new TypedObject(DataType.VECTOR_OBJECT, obj);
        }

        throw new RuntimeException("Can't encode type of " + obj);
    }
}

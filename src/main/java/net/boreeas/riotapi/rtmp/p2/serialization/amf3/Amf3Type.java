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

package net.boreeas.riotapi.rtmp.p2.serialization.amf3;

import net.boreeas.riotapi.rtmp.p2.serialization.AmfType;
import net.boreeas.riotapi.rtmp.p2.serialization.SerializationContext;

import java.util.Date;
import java.util.Map;

/**
 * Created on 4/15/2014.
 */
public enum Amf3Type {
    UNDEFINED,          // 0x00
    NULL,               // 0x01
    TRUE,               // 0x02
    FALSE,              // 0x03
    INTEGER,            // 0x04
    DOUBLE,             // 0x05
    STRING,             // 0x06
    XML_DOC,            // 0x07
    DATE,               // 0x08
    ARRAY,              // 0x09
    OBJECT,             // 0x0A
    XML,                // 0x0B
    BYTE_ARRAY,         // 0x0C
    VECTOR_INT,         // 0x0D
    VECTOR_UINT,        // 0x0E
    VECTOR_DOUBLE,      // 0x0F
    VECTOR_OBJECT,      // 0x10
    DICTIONARY;         // 0x11

    public static Amf3Type getTypeForObject(Object o) {
        if (o == null) return NULL;
        if (o.getClass().isAnnotationPresent(AmfType.class)) return o.getClass().getAnnotation(AmfType.class).amf3Type();
        if (o instanceof Boolean) return ((Boolean) o) ? TRUE : FALSE;
        if (o instanceof Integer || o instanceof Byte || o instanceof Short) return INTEGER;
        if (o instanceof Long || o instanceof Double) return DOUBLE;
        if (o instanceof String) return STRING;
        if (o instanceof Map) return DICTIONARY;
        if (o instanceof Date) return DATE;
        if (o instanceof byte[]) return BYTE_ARRAY;
        if (o instanceof int[]) return VECTOR_INT;
        if (o instanceof long[]) return VECTOR_UINT;
        if (o instanceof double[]) return VECTOR_DOUBLE;
        if (o instanceof Object[]) return VECTOR_OBJECT;
        if (o.getClass().isAnnotationPresent(SerializationContext.class)) return OBJECT;
        return UNDEFINED;
    }
}

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

import net.boreeas.riotapi.rtmp.serialization.AmfType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created on 4/15/2014.
 */
public enum Amf3Type {
    UNDEFINED,          // 0x00
    NULL,               // 0x01
    FALSE,              // 0x02
    TRUE,               // 0x03
    INTEGER,            // 0x04
    DOUBLE,             // 0x05
    STRING(true),       // 0x06
    XML_DOC(true),      // 0x07
    DATE(true),         // 0x08
    ARRAY(true),        // 0x09
    OBJECT(true),       // 0x0A
    XML(true),          // 0x0B
    BYTE_ARRAY(true),   // 0x0C
    VECTOR_INT(true),   // 0x0D
    VECTOR_UINT(true),  // 0x0E
    VECTOR_DOUBLE(true),// 0x0F
    VECTOR_OBJECT(true),// 0x10
    DICTIONARY(true);   // 0x11


    public final boolean referencable;

    private Amf3Type() { this(false); }
    private Amf3Type(boolean referencable) { this.referencable = referencable; }

    public static Amf3Type getTypeForObject(Object o) {
        if (o == null) return NULL;
        if (o.getClass().isAnnotationPresent(AmfType.class)) return o.getClass().getAnnotation(AmfType.class).amf3Type();
        if (o instanceof Boolean) return ((Boolean) o) ? TRUE : FALSE;
        if (o instanceof Integer || o instanceof Byte || o instanceof Short) return INTEGER;
        if (o instanceof Long || o instanceof Double) return DOUBLE;
        if (o instanceof String || o instanceof UUID || o instanceof Enum) return STRING;
        if (o instanceof Map || o instanceof List) return ARRAY;
        if (o instanceof Date) return DATE;
        if (o instanceof byte[]) return BYTE_ARRAY;
        // Riot's servers don't support vectors
        //if (o instanceof int[]) return VECTOR_INT;
        //if (o instanceof long[]) return VECTOR_UINT;
        //if (o instanceof double[]) return VECTOR_DOUBLE;
        if (o.getClass().isArray()) return ARRAY;
        return OBJECT;
    }
}

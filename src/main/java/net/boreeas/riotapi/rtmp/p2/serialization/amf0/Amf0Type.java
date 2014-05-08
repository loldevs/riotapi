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

package net.boreeas.riotapi.rtmp.p2.serialization.amf0;

import net.boreeas.riotapi.rtmp.p2.serialization.AmfType;
import net.boreeas.riotapi.rtmp.p2.serialization.SerializationContext;

import java.util.Date;
import java.util.Map;

/**
 * Created on 5/7/2014.
 */
public enum Amf0Type {
    NUMBER,         // 0x00
    BOOLEAN,        // 0x01
    STRING,         // 0x02
    OBJECT,         // 0x03
    MOVIECLIP,      // 0x04
    NULL,           // 0x05
    UNDEFINED,      // 0x06
    REFERENCE,      // 0x07
    ECMA_ARRAY,     // 0x08
    OBJECT_END,     // 0x09
    STRICT_ARRAY,   // 0x0a
    DATE,           // 0x0b
    LONG_STRING,    // 0x0c
    UNSUPPORTED,    // 0x0d
    RECORDSET,      // 0x0e
    XML,            // 0x0f
    TYPED_OBJECT,   // 0x10
    AMF3_OBJECT;    // 0x11

    public static Amf0Type getTypeForObject(Object obj) {
        if (obj == null) return NULL;
        if (obj.getClass().isAnnotationPresent(AmfType.class)) return obj.getClass().getAnnotation(AmfType.class).amf0Type();
        if (obj instanceof Number) return NUMBER;
        if (obj instanceof Boolean) return BOOLEAN;
        if (obj instanceof String) return ((String)obj).length() > Short.MAX_VALUE ? LONG_STRING : STRING;
        if (obj instanceof Object[]) return STRICT_ARRAY;
        if (obj instanceof Map) return ECMA_ARRAY;
        if (obj instanceof Date) return DATE;
        if (obj.getClass().isAnnotationPresent(SerializationContext.class)) return OBJECT;
        return UNDEFINED;
    }
}

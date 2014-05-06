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

package net.boreeas.riotapi.rtmp.p2;

import lombok.SneakyThrows;
import net.boreeas.riotapi.rtmp.amf.TraitDefinition;
import net.boreeas.riotapi.rtmp.p2.serialization.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 5/2/2014.
 */
public class AmfWriter {

    private Map<Class, AmfSerializer> amf3Serializers = new HashMap<>();
    private Map<Object, Integer> stringRefTable = new HashMap<>();
    private Map<Object, Integer> objectRefTable = new HashMap<>();
    private Map<TraitDefinition, Integer> traitRefTable = new HashMap<>();
    private OutputStream out;

    public AmfWriter(OutputStream out) {
        this.out = out;
    }

    public AmfWriter() {
        amf3Serializers.put(Integer.class, new Amf3IntegerSerializer());
        amf3Serializers.put(Double.class, new Amf3DoubleSerializer());
        amf3Serializers.put(Long.class, new Amf3LongSerializer());
        amf3Serializers.put(Boolean.class, (o1, o2) -> {}); // Boolean value is indicated by type marker
        amf3Serializers.put(String.class, new Amf3StringSerializer(this));
        amf3Serializers.put(Date.class, new Amf3DateSerializer(this));
        amf3Serializers.put(Map.class, new Amf3DictSerializer(this));
        amf3Serializers.put(byte[].class, new Amf3ByteArraySerializer(this));
        amf3Serializers.put(int[].class, new Amf3IntVectorSerializer(this));
        amf3Serializers.put(long[].class, new Amf3UintVectorSerializer(this));
        amf3Serializers.put(double[].class, new Amf3DoubleVectorSerializer(this));
        amf3Serializers.put(Object[].class, new Amf3ObjectVectorSerializer(this));
    }

    public <T> void registerAmf3Serializer(Class<T> targetClass, AmfSerializer<T> serializer) {
        this.amf3Serializers.put(targetClass, serializer);
    }

    /**
     * Encodes an object in amf3. Prepends a type marker
     * @param o The object to encode
     * @throws IOException
     */
    public void encodeAmf3(Object o) throws IOException {
        encodeAmf3(o, Amf3Type.getTypeForObject(o));
    }

    /**
     * Encodes an object in amf3 as an object of
     * the specified type
     * @param obj The object to encode
     * @param marker The type marker for this object
     * @throws IOException
     */
    public void encodeAmf3(Object obj, Amf3Type marker) throws IOException {
        out.write(marker.ordinal());
        if (obj == null) {
            return; // Null marker is enough
        }

        Map<Object, Integer> refTable = marker == Amf3Type.STRING ? stringRefTable : objectRefTable;

        if (marker == Amf3Type.STRING && ((String) obj).isEmpty()) {    // Never reference empty strings

            serializeAmf3(obj);
        } else if (!writeRefIfAlreadyReferenced(obj, refTable)) {       // Writes ref id if exists

            refTable.put(obj, refTable.size());
            serializeAmf3(obj);
        }
    }

    private boolean writeRefIfAlreadyReferenced(Object o, Map<Object, Integer> refTable) throws IOException {
        if (refTable.containsKey(o)) {
            serializeAmf3(refTable.get(o) << 1);
            return true;
        }

        return false;
    }

    /**
     * Serializes the specified object to amf3
     * @param o The object to serialize
     * @throws IOException
     */
    @SneakyThrows(value = {InstantiationException.class, IllegalAccessException.class})
    public void serializeAmf3(Object o) throws IOException {
        System.out.println("Serializing " + o + "/" + o.getClass());

        if (amf3Serializers.containsKey(o.getClass())) {
            amf3Serializers.get(o.getClass()).serialize(o, out);

        } else if (o.getClass().isAnnotationPresent(SerializationContext.class)) {

            SerializationContext annotation = o.getClass().getAnnotation(SerializationContext.class);
            Amf3ObjectSerializer serializer = null;

            try {
                serializer = annotation.serializerAmf3().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            serializer.setTraitRefTable(traitRefTable);
            serializer.serialize(o, out);
        } else {
            throw new IllegalArgumentException("Can't serialize " + o.getClass());
        }
    }
}

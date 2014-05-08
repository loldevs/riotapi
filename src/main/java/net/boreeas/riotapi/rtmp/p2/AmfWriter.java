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

import lombok.NonNull;
import lombok.SneakyThrows;
import net.boreeas.riotapi.rtmp.amf.TraitDefinition;
import net.boreeas.riotapi.rtmp.p2.serialization.AmfSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.ObjectEncoding;
import net.boreeas.riotapi.rtmp.p2.serialization.SerializationContext;
import net.boreeas.riotapi.rtmp.p2.serialization.amf0.Amf0IntegerSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf0.Amf0Type;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3ByteArraySerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3DateSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3DictSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3DoubleSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3DoubleVectorSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3IntVectorSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3IntegerSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3LongSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3ObjectSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3ObjectVectorSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3StringSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3Type;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3UintVectorSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 5/2/2014.
 */
public class AmfWriter {

    private OutputStream out;
    private ObjectEncoding encoding;

    private Map<Class, AmfSerializer> amf3Serializers = new HashMap<>();
    private Map<Object, Integer> amf3StringRefTable = new HashMap<>();
    private Map<Object, Integer> amf3ObjectRefTable = new HashMap<>();
    private Map<TraitDefinition, Integer> traitRefTable = new HashMap<>();

    private Map<Class, AmfSerializer> amf0Serializers = new HashMap<>();
    private Map<Object, Short> amf0ObjectRefTable = new HashMap<>();

    public AmfWriter(OutputStream out) {
        this(out, ObjectEncoding.AMF3);
    }

    public AmfWriter(OutputStream out, ObjectEncoding encoding) {
        this.out = out;
        this.encoding = encoding;

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

        amf0Serializers.put(Integer.class, new Amf0IntegerSerializer());
    }

    public void encode(Object object) throws IOException {
        encode(object, encoding);
    }

    public void encode(Object obj, ObjectEncoding encoding) throws IOException {
        Amf0Type type = Amf0Type.getTypeForObject(obj);

        if (type == Amf0Type.NULL) {
            out.write(type.ordinal());
            return;
        }

        if (type == Amf0Type.AMF3_OBJECT || encoding == ObjectEncoding.AMF3) {
            out.write(Amf0Type.AMF3_OBJECT.ordinal());
            encodeAmf3(obj);
        } else {
            encodeAmf0(obj);
        }
    }

    // <editor-fold desc="Amf0">
    public void encodeAmf0(Object obj) throws IOException {
        encodeAmf0(obj, Amf0Type.getTypeForObject(obj));
    }

    public void encodeAmf0(Object obj, Amf0Type type) throws IOException {
        if (type == Amf0Type.NULL) { // Send null marker only
            out.write(type.ordinal());
            return;
        }

        if (amf0ObjectRefTable.containsKey(obj)) { // Send ref instead of object
            out.write(Amf0Type.REFERENCE.ordinal());
            out.serializeAmf0(amf0ObjectRefTable.get(obj));
            return;
        }

        amf0ObjectRefTable.put(obj, (short) amf0ObjectRefTable.size());
        out.write(type.ordinal());
        serializeAmf0(obj);
     }

    public void serializeAmf0(Object obj) throws IOException {
        System.out.println("Serializing [0] " + obj.getClass() + "/" + obj);

        if (amf0Serializers.containsKey(obj.getClass())) {
            amf0Serializers.get(obj.getClass()).serialize(obj, out);

        } else if (obj.getClass().isAnnotationPresent(SerializationContext.class)) {

            SerializationContext context = obj.getClass().getAnnotation(SerializationContext.class);
            Amf0ObjectSerializer serializer = context.serializerAmf0().newInstance();

            serializer.setWriter(this);
            serializer.serialize(obj, out);
        } else {
            throw new IllegalArgumentException("Can't serialize " + obj.getClass());
        }
    }

    public <T> void registerAmf0Serializer(@NonNull Class<T> targetClass, AmfSerializer<T> serializer) {
        this.amf0Serializers.put(targetClass, serializer);
    }

    // </editor-fold>

    // <editor-fold desc="Amf3">

    public <T> void registerAmf3Serializer(@NonNull Class<T> targetClass, AmfSerializer<T> serializer) {
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
        if (marker == Amf3Type.NULL) {
            return; // Null marker is enough
        }

        Map<Object, Integer> refTable = marker == Amf3Type.STRING ? amf3StringRefTable : amf3ObjectRefTable;

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
        System.out.println("Serializing [3] " + o.getClass() + "/" + o);

        if (amf3Serializers.containsKey(o.getClass())) {
            amf3Serializers.get(o.getClass()).serialize(o, out);

        } else if (o.getClass().isAnnotationPresent(SerializationContext.class)) {

            SerializationContext annotation = o.getClass().getAnnotation(SerializationContext.class);
            Amf3ObjectSerializer serializer = annotation.serializerAmf3().newInstance();

            serializer.setTraitRefTable(traitRefTable);
            serializer.setWriter(this);
            serializer.serialize(o, out);
        } else {
            throw new IllegalArgumentException("Can't serialize " + o.getClass());
        }
    }
    // </editor-fold>


}

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

import lombok.Delegate;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.boreeas.riotapi.Util;
import net.boreeas.riotapi.rtmp.serialization.amf0.*;
import net.boreeas.riotapi.rtmp.serialization.amf3.*;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created on 5/2/2014.
 */
public class AmfWriter extends OutputStream implements ObjectOutput {

    @Delegate private DataOutputStream out;
    private ObjectEncoding encoding;

    private Map<Class, AmfSerializer> amf3Serializers = new HashMap<>();
    private Map<Object, Integer> amf3StringRefTable = new HashMap<>();
    private Map<Object, Integer> amf3ObjectRefTable = new HashMap<>();
    private Map<TraitDefinition, Integer> traitRefTable = new HashMap<>();
    private Map<Object, TraitDefinition> traitDefCache = new HashMap<>();

    private Map<Class, AmfSerializer> amf0Serializers = new HashMap<>();
    private Map<Object, Short> amf0ObjectRefTable = new HashMap<>();

    public AmfWriter(OutputStream out) {
        this(out, ObjectEncoding.AMF3);
    }

    public AmfWriter(OutputStream out, ObjectEncoding encoding) {
        this.out = new DataOutputStream(out);
        this.encoding = encoding;

        amf3Serializers.put(Integer.class, Amf3IntegerSerializer.INSTANCE);
        amf3Serializers.put(Double.class, Amf3NumberSerializer.INSTANCE);
        amf3Serializers.put(Long.class, Amf3NumberSerializer.INSTANCE);
        amf3Serializers.put(Boolean.class, (o1, o2) -> {}); // Boolean value is indicated by type marker
        amf3Serializers.put(String.class, new Amf3StringSerializer(this));
        amf3Serializers.put(Date.class, new Amf3DateSerializer(this));
        amf3Serializers.put(byte[].class, new Amf3ByteArraySerializer(this));
        amf3Serializers.put(ArrayList.class, (al, o) -> new Amf3ArraySerializer(this).serialize(((List)al).toArray(), o));
        amf3Serializers.put(HashMap.class, new Amf3ArraySerializer(this));
        amf3Serializers.put(UUID.class, (uuid, o) -> new Amf3StringSerializer(this).serialize(uuid.toString(), o));

        amf0Serializers.put(Integer.class, Amf0NumberSerializer.INSTANCE);
        amf0Serializers.put(Byte.class, Amf0NumberSerializer.INSTANCE);
        amf0Serializers.put(Short.class, Amf0NumberSerializer.INSTANCE);
        amf0Serializers.put(Long.class, Amf0NumberSerializer.INSTANCE);
        amf0Serializers.put(Double.class, Amf0NumberSerializer.INSTANCE);
        amf0Serializers.put(Float.class, Amf0NumberSerializer.INSTANCE);
        amf0Serializers.put(Boolean.class, (bool, os) -> os.write(bool == Boolean.TRUE ? 1 : 0));
        amf0Serializers.put(String.class, Amf0StringSerializer.INSTANCE);
        amf0Serializers.put(Character.class, (c, os) -> Amf0StringSerializer.INSTANCE.serialize(""+c, os));
        amf0Serializers.put(UUID.class, (uuid, os) -> Amf0StringSerializer.INSTANCE.serialize(uuid.toString(), os));
        amf0Serializers.put(Date.class, Amf0DateSerializer.INSTANCE);
        amf0Serializers.put(HashMap.class, new Amf0MapSerializer(this));

    }

    /**
     * Enables the uses of the Vector and Dictionary type markers (defaults
     * to array serialization otherwise).
     */
    public void enableExtendedSerializers() {
        amf3Serializers.put(int[].class, new Amf3IntVectorSerializer(this));
        amf3Serializers.put(long[].class, new Amf3UintVectorSerializer(this));
        amf3Serializers.put(double[].class, new Amf3DoubleVectorSerializer(this));
        amf3Serializers.put(ArrayList.class, new Amf3VectorSerializer(this));
        amf3Serializers.put(HashMap.class, new Amf3DictSerializer(this));
    }

    // <editor-fold desc="Convenience">
    public void writeUint24(int i) throws IOException {
        write(i >> 16);
        write(i >> 8);
        write(i);
    }

    public void writeLittleEndianInt(int i) throws IOException {
        write(i);
        write(i >> 8);
        write(i >> 16);
        write(i >> 24);
    }
    // </editor-fold>

    public void writeObject(Object object) throws IOException {
        if (encoding == ObjectEncoding.AMF3) {
            encodeAmf3(object); // Skip AMF0 -> AMF3 marker
        } else {
            encodeAmf0(object);
        }
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

        if (type.referencable) {
            // These types can be sent by reference
            if (amf0ObjectRefTable.containsKey(obj)) { // Send ref instead of object
                out.write(Amf0Type.REFERENCE.ordinal());
                serializeAmf0(amf0ObjectRefTable.get(obj));
                return;
            }

            amf0ObjectRefTable.put(obj, (short) amf0ObjectRefTable.size());
        }

        out.write(type.ordinal());
        serializeAmf0(obj);
     }

    @SneakyThrows(value = {InstantiationException.class, IllegalAccessException.class})
    public void serializeAmf0(Object obj) throws IOException {

        Serialization context;
        if (amf0Serializers.containsKey(obj.getClass())) {
            amf0Serializers.get(obj.getClass()).serialize(obj, out);

        } else if ((context = Util.searchClassHierarchy(obj.getClass(), Serialization.class)) != null) {
            Amf0ObjectSerializer serializer = context.amf0Serializer().newInstance();

            serializer.setWriter(this);
            serializer.serialize(obj, out);
        } else if (obj.getClass().isArray()) {
            // Arrays require special handling
            serializeArrayAmf0(obj);
        } else if (obj instanceof Enum) {
            serializeAmf0(((Enum) obj).name());
        } else {
            Amf0ObjectSerializer serializer = new Amf0ObjectSerializer();
            serializer.setWriter(this);
            serializer.serialize(obj, out);
        }

        out.flush();
    }

    private void serializeArrayAmf0(Object obj) throws IOException {
        int len = Array.getLength(obj);
        out.write(len >> 24);
        out.write(len >> 16);
        out.write(len >> 8);
        out.write(len);

        for (int i = 0; i < len; i++) {
            encodeAmf0(Array.get(obj, i));
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

        serializeAmf3(obj, marker);
    }

    private boolean checkReferenceable(Object obj, Amf3Type marker) throws IOException {

        Map<Object, Integer> refTable = marker == Amf3Type.STRING ? amf3StringRefTable : amf3ObjectRefTable;

        if ((marker == Amf3Type.STRING && obj.toString().isEmpty()) // Never reference empty strings
                || !marker.referencable) {

            return false;
        } else if (!writeRefIfAlreadyReferenced(obj, refTable)) {       // Writes ref id if exists
            refTable.put(obj, refTable.size());
            return false;
        }

        return true;
    }

    private boolean writeRefIfAlreadyReferenced(Object o, Map<Object, Integer> refTable) throws IOException {
        if (refTable.containsKey(o)) {
            serializeAmf3(refTable.get(o) << 1);
            return true;
        }

        return false;
    }

    public void serializeAmf3(Object obj) throws IOException {
        serializeAmf3(obj, Amf3Type.getTypeForObject(obj));
    }

    /**
     * Serializes the specified object to amf3
     * @param obj The object to serialize
     * @param marker The type of the object to check for referencability
     * @throws IOException
     */
    @SneakyThrows(value = {InstantiationException.class, IllegalAccessException.class})
    public void serializeAmf3(Object obj, Amf3Type marker) throws IOException {


        if (checkReferenceable(obj, marker)) {
            return;
        }

        Serialization context;
        if (amf3Serializers.containsKey(obj.getClass())) {
            amf3Serializers.get(obj.getClass()).serialize(obj, out);

        } else if ((context = Util.searchClassHierarchy(obj.getClass(), Serialization.class)) != null && !context.deserializeOnly()) {

            Amf3ObjectSerializer serializer = context.amf3Serializer().newInstance();

            serializer.setTraitRefTable(traitRefTable);
            serializer.setTraitDefCache(traitDefCache);
            serializer.setWriter(this);
            serializer.serialize(obj, out);
        } else if (obj.getClass().isArray()) {
            // Arrays require special handling
            serializeArrayAmf3(obj);
        } else if (obj instanceof Enum) {
            // Enums are written by name
            serializeAmf3(((Enum) obj).name());
        } else {

            Amf3ObjectSerializer serializer = new Amf3ObjectSerializer();
            serializer.setTraitRefTable(traitRefTable);
            serializer.setTraitDefCache(traitDefCache);
            serializer.setWriter(this);
            serializer.serialize(obj, out);
        }

        out.flush();
    }

    private void serializeArrayAmf3(Object obj) throws IOException {
        /*
        // Serialization as Vec<Object>
        int length = Array.getLength(obj);
        serializeAmf3(length << 1 | 1);
        out.write(1);       // Fixed size
        serializeAmf3("*"); // Dynamic type

        for (int i = 0; i < length; i++) {
            encodeAmf3(Array.get(obj, i));
        }
        */

        // Serialization as Object[]
        new Amf3ArraySerializer(this).serialize(obj, out);
    }

    // </editor-fold>
}

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

import lombok.*;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.rtmp.serialization.amf0.Amf0ObjectDeserializer;
import net.boreeas.riotapi.rtmp.serialization.amf0.Amf0Type;
import net.boreeas.riotapi.rtmp.serialization.amf3.Amf3ObjectDeserializer;
import net.boreeas.riotapi.rtmp.serialization.amf3.Amf3Type;
import net.boreeas.riotapi.rtmp.serialization.amf3.Dynamic;
import org.reflections.Reflections;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created on 5/13/2014.
 */
@Log4j
public class AmfReader extends InputStream implements ObjectInput {

    @Delegate private DataInputStream in;
    @Setter private ObjectEncoding encoding;
    private Reflections reflections;
    private Map<String, Class<?>> serializableClasses;

    private List<Object> amf0ObjectReferences = new ArrayList<>();
    private List<Callable<Object>> amf0Deserializers = new ArrayList<>();

    private List<Object> amf3ObjectReferences = new ArrayList<>();
    private List<String> amf3StringReferences = new ArrayList<>();
    private List<TraitDefinition> amf3ClassReferences = new ArrayList<>();
    private List<Callable<Object>> amf3Deserializers = new ArrayList<>();
    private Map<String, Amf3ObjectDeserializer> amf3ObjectDeserializers = new HashMap<>();

    public AmfReader(InputStream in) {
        this(in, ObjectEncoding.AMF3, "net.boreeas.riotapi"); // By default, look for classes from this package
    }

    public AmfReader(InputStream in, ObjectEncoding encoding, String... packages) {
        this.in = new DataInputStream(in);
        this.reflections = new Reflections(packages);
        this.encoding = encoding;
        addDeserializers();
        buildSerializableClassList();
    }

    /**
     * Creates an input stream without rescanning the classpath
     * @param in The inputstream to read from
     * @param base The reader from which to take the release of serializable classes
     */
    public AmfReader(InputStream in, AmfReader base) {
        this.in = new DataInputStream(in);
        this.reflections = base.reflections;
        addDeserializers();
        this.serializableClasses = base.serializableClasses;
        this.encoding = base.encoding;
    }

    private void addDeserializers() {
        amf0Deserializers.add(in::readDouble);
        amf0Deserializers.add(in::readBoolean);
        amf0Deserializers.add(in::readUTF);
        amf0Deserializers.add(this::readAmf0AnonymousObject);
        amf0Deserializers.add(() -> { throw new UnsupportedOperationException("Movieclip"); });
        amf0Deserializers.add(() -> null);     // null
        amf0Deserializers.add(() -> null);     // undefined
        amf0Deserializers.add(this::readAmf0Reference);
        amf0Deserializers.add(this::readAmf0Map);
        amf0Deserializers.add(() -> { throw new IllegalArgumentException("End marker can't be deserialized"); });
        amf0Deserializers.add(this::readAmf0Array);
        amf0Deserializers.add(this::readAmf0Date);
        amf0Deserializers.add(this::readAmf0LongString);
        amf0Deserializers.add(() -> { throw new UnsupportedOperationException("Unsupported"); });
        amf0Deserializers.add(() -> { throw new UnsupportedOperationException("Recordset"); });
        amf0Deserializers.add(() -> { throw new UnsupportedOperationException("XML"); });
        amf0Deserializers.add(this::readAmf0Object);
        amf0Deserializers.add(this::decodeAmf3);


        amf3Deserializers.add(() -> { throw new UnsupportedOperationException("Undefined"); });
        amf3Deserializers.add(() -> null);
        amf3Deserializers.add(() -> false);
        amf3Deserializers.add(() -> true);
        amf3Deserializers.add(this::readInt29);
        amf3Deserializers.add(in::readDouble);
        amf3Deserializers.add(this::readAmf3String);
        amf3Deserializers.add(() -> { throw new UnsupportedOperationException("XML Doc"); });
        amf3Deserializers.add(this::readAmf3Date);
        amf3Deserializers.add(this::readAmf3Array);
        amf3Deserializers.add(this::readAmf3Object);
        amf3Deserializers.add(() -> { throw new UnsupportedOperationException("XML"); });
        amf3Deserializers.add(this::readAmf3ByteArray);
        amf3Deserializers.add(this::readAmf3VectorInt);
        amf3Deserializers.add(this::readAmf3VectorUint);
        amf3Deserializers.add(this::readAmf3VectorDouble);
        amf3Deserializers.add(this::readAmf3VectorObject);
        amf3Deserializers.add(this::readAmf3Map);
    }

    @Override
    public Object readObject() throws ClassNotFoundException, IOException {
        if (this.encoding == ObjectEncoding.AMF0) {
            return decodeAmf0();
        } else {
            return decodeAmf3();
        }
    }

    public void addObjectDeserializer(String type, Amf3ObjectDeserializer deserializer) {
        amf3ObjectDeserializers.put(type, deserializer);
        deserializer.setObjectRefTable(amf3ObjectReferences);
    }

    public void scanPackages(String... packages) {
        reflections.merge(new Reflections(packages));
        buildSerializableClassList();
    }

    private synchronized void buildSerializableClassList() {
        this.serializableClasses = new HashMap<>();
        reflections.getTypesAnnotatedWith(Serialization.class).forEach(cls -> {
            Serialization s = cls.getAnnotation(Serialization.class);

            serializableClasses.put(s.name(), cls);
            for (String alt: s.noncanonicalNames()) {
                serializableClasses.put(alt, cls);
            }
        });
    }


    // <editor-fold desc="Convenience methods">
    public int readUint24() throws IOException {
        return read() << 16 | read() << 8 | read();
    }

    public int readIntLittleEndian() throws IOException {
        return read() | read() << 8 | read() << 16 | read() << 24;
    }
    // </editor-fold>

    // <editor-fold desc="Amf0">
    public <T> T decodeAmf0() throws IOException {
        return deserializeAmf0(in.read());
    }

    public <T> T deserializeAmf0(@NonNull Amf0Type type) throws IOException {
        return deserializeAmf0(type.ordinal());
    }

    private <T> T  deserializeAmf0(int type) throws IOException {
        try {
            return (T) amf0Deserializers.get(type).call();
        } catch (Exception ex) {
            throw new IOException("Error during deserialization", ex);
        }
    }


    // <editor-fold desc="Amf0 Deserializer">
    private <T> T  readAmf0Reference() throws IOException {
        return (T) amf0ObjectReferences.get(in.readShort());
    }

    public Date readAmf0Date() throws IOException {
        long time = (long) in.readDouble();
        in.readShort(); // timezone info - unused
        return new Date(time);
    }

    public String readAmf0LongString() throws IOException {
        int length = in.readInt();
        byte[] buffer = new byte[length];
        in.read(buffer);
        return new String(buffer, "UTF-8");
    }

    public Object[] readAmf0Array() throws IOException {
        int length = in.readInt();

        Object[] arr = new Object[length];
        for (int i = 0; i < length; i++) {
            arr[i] = decodeAmf0();
        }

        amf0ObjectReferences.add(arr);
        return arr;
    }

    public Map<String, Object> readAmf0KeyValuePairs() throws IOException {
        Map<String, Object> map = new HashMap<>();

        while (true) {
            String name = in.readUTF();
            int type = in.read();
            if (type == Amf0Type.OBJECT_END.ordinal()) { break; }

            Object obj = deserializeAmf0(type);
            map.put(name, obj);
        }

        return map;
    }

    public Map<String, Object> readAmf0Map() throws IOException {
        int length = in.readInt();
        Map<String, Object> result = readAmf0KeyValuePairs();
        amf0ObjectReferences.add(result);
        return result;
    }

    @SneakyThrows({IllegalAccessException.class, InstantiationException.class})
    public Object readAmf0Object() throws IOException {
        String name = in.readUTF();
        Class<?> cls = serializableClasses.get(name);
        Object result;

        if (cls == null) {
            Map<String, Object> kvPairs = readAmf0KeyValuePairs();
            result = new AmfObject(name, kvPairs);
        } else {
            Amf0ObjectDeserializer deserializer = cls.getAnnotation(Serialization.class).amf0Deserializer().newInstance();
            deserializer.setCls(cls);
            result = deserializer.deserialize(this);
        }

        amf0ObjectReferences.add(result);
        return result;
    }

    public AmfObject readAmf0AnonymousObject() throws IOException {
        AmfObject obj = new AmfObject(null, readAmf0KeyValuePairs());
        amf0ObjectReferences.add(obj);

        return obj;
    }
    // </editor-fold>

    // </editor-fold>

    // <editor-fold desc="Amf3">
    public <T> T decodeAmf3() throws IOException {
        int type = in.read();
        return deserializeAmf3(type);
    }

    public <T> T deserializeAmf3(@NonNull Amf3Type type) throws IOException {
        return deserializeAmf3(type.ordinal());
    }

    private <T> T deserializeAmf3(int type) throws IOException {
        try {
            return (T) amf3Deserializers.get(type).call();
        } catch (Exception ex) {
            throw new IOException("Error during deserialization", ex);
        }
    }

    private Amf3Header readAmf3Header() throws IOException {
        int value = readInt29();
        return new Amf3Header(value >> 1, (value & 1) == 0);
    }

    public int readInt29() throws IOException {
        // first byte
        int total = in.read();
        if (total < 128) {
            return total;
        }


        total = (total & 0x7f) << 7;
        // second byte
        int nextByte = in.read();

        if (nextByte < 128) {
            total = total | nextByte;
        } else {
            total = (total | nextByte & 0x7f) << 7;
            // third byte
            nextByte = in.read();
            if (nextByte < 128) {
                total = total | nextByte;
            } else {
                total = (total | nextByte & 0x7f) << 8;
                // fourth byte
                nextByte = in.read();
                total = total | nextByte;
            }
        }

        int mask = 1 << 28; // 29th bit holds sign for int29
        return -(total & mask) | total; // -(result & mask) sets all bit above the mask if the bit at the mask is release
    }

    public Date readAmf3Date() throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) {
            return getAmf3Reference(header);
        }

        Date d = new Date((long) in.readDouble());
        amf3ObjectReferences.add(d);

        return d;
    }

    public String readAmf3String() throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return amf3StringReferences.get(header.value);
        if (header.value == 0)  return "";  // Empty string

        byte[] buffer = new byte[header.value];
        in.read(buffer);
        String result = new String(buffer, "UTF-8");

        amf3StringReferences.add(result);
        return result;
    }

    public byte[] readAmf3ByteArray() throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return getAmf3Reference(header);

        byte[] buffer = new byte[header.value];
        in.read(buffer);

        amf3ObjectReferences.add(buffer);
        return buffer;
    }

    public Map<Object, Object> readAmf3Array() throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return getAmf3Reference(header);

        Map<Object, Object> result = new HashMap<>();
        amf3ObjectReferences.add(result); // Add here for reference order

        String key;
        while (!(key = readAmf3String()).isEmpty()) {
            result.put(key, decodeAmf3());
        }

        for (int i = 0; i < header.value; i++) {
            result.put(i, decodeAmf3());
        }

        return result;
    }

    private <T> List<T> readAmf3Vector(boolean typed, Callable<T> readFunc) throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return getAmf3Reference(header);

        boolean fixedSize = in.readBoolean();
        String type = typed ? readAmf3String() : null;

        List<T> result = new ArrayList<>();
        amf3ObjectReferences.add(result);  // Add here for reference order

        for (int i = 0; i < header.value; i++) {
            try {
                result.add(readFunc.call());
            } catch (Exception e) {
                throw new IOException("Error during deserialization", e);
            }
        }

        return result;
    }

    public Object readAmf3VectorInt() throws IOException {
        return readAmf3Vector(false, in::readInt);
    }

    public Object readAmf3VectorUint() throws IOException {
        return readAmf3Vector(false, () -> ((long) in.readInt()) & 0xffffffffL);
    }

    public Object readAmf3VectorDouble() throws IOException {
        return readAmf3Vector(false, in::readDouble);
    }

    public Object readAmf3VectorObject() throws IOException {
        return readAmf3Vector(true, this::decodeAmf3);
    }

    public Map<Object, Object> readAmf3Map() throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return getAmf3Reference(header);

        boolean weakReferences = in.readBoolean();

        Map<Object, Object> result = new HashMap<>();
        amf3ObjectReferences.add(result);  // Add here for reference order

        for (int i = 0; i < header.value; i++) {
            result.put(decodeAmf3(), decodeAmf3());
        }

        return result;
    }

    private <T> T getAmf3Reference(Amf3Header header) {
        return (T) amf3ObjectReferences.get(header.value);
    }

    @SneakyThrows({IllegalAccessException.class, InstantiationException.class})
    public Object readAmf3Object() throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return getAmf3Reference(header);

        TraitDefinition traitDef = readAmf3TraitDefinition(header.value);
        String type = traitDef.getName();

        Object result;
        if (amf3ObjectDeserializers.containsKey(type)) {
            result = amf3ObjectDeserializers.get(type).deserialize(this, traitDef);
        } else if (type.isEmpty()) {
            AnonymousAmfObject amfObj = new AnonymousAmfObject();
            result = amfObj;
            amf3ObjectReferences.add(result);

            for (FieldRef s: traitDef.getStaticFields()) {
                amfObj.put(s.getName(), decodeAmf3());
            }

            if (traitDef.isDynamic()) {
                String key;
                while (!(key = readAmf3String()).isEmpty()) {
                    amfObj.put(key, decodeAmf3());
                }
            }

        } else if (serializableClasses.containsKey(type)) {
            Class<?> c = serializableClasses.get(type);

            Serialization context = c.getAnnotation(Serialization.class);
            Amf3ObjectDeserializer deserializer = context.amf3Deserializer().newInstance();
            deserializer.setCls(c);
            deserializer.setObjectRefTable(amf3ObjectReferences);

            result = deserializer.deserialize(this, traitDef);

        } else {
            AmfObject amfObj = new AmfObject(type);
            result = amfObj;
            amf3ObjectReferences.add(result);

            for (FieldRef s: traitDef.getStaticFields()) {
                amfObj.set(s.getName(), decodeAmf3());
            }

            if (traitDef.isDynamic()) {
                String key;
                while (!(key = readAmf3String()).isEmpty()) {
                    amfObj.set(key, decodeAmf3());
                }
            }

        }

        return result;
    }


    private TraitDefinition readAmf3TraitDefinition(int flags) throws IOException {
        if ((flags & 1) == 0) { // Reference
            return amf3ClassReferences.get(flags >> 1);
        }

        String type = readAmf3String();
        boolean externalizable = ((flags >> 1) & 1) == 1;
        boolean dynamic = ((flags >> 2) & 1) == 1;

        TraitDefinition def = new TraitDefinition(type, dynamic, externalizable);
        String[] members = new String[flags >> 3];

        Class match = type.isEmpty() ? AnonymousAmfObject.class : serializableClasses.get(type);
        log.trace("Reading trait definition for '" + type + "' (" + ((match == null) ? "no match" : "has match in " + match) + ")");


        for (int i = 0; i < members.length; i++) {
            String fieldName = readAmf3String();
            FieldRef field;

            if (match != null) {
                field = scanForField(match, fieldName);
            } else {
                field = new FieldRef(fieldName, fieldName, null);
            }

            if (field == null) {
                if (fieldName.equals("futureData") || fieldName.equals("dataVersion")
                        || fieldName.equals("DSSubtopic") || fieldName.equals("DSId") || fieldName.equals("DSMessagingVersion")) {
                    log.trace("No match for field " + (type.isEmpty() ? "<anonymous>" : type) + "." + fieldName + " in " + match);
                } else {
                    log.warn("No match for field " + (type.isEmpty() ? "<anonymous>" : type) + "." + fieldName + " in " + match);
                }
                def.getStaticFields().add(new FieldRef(null, fieldName, null));
            } else {
                def.getStaticFields().add(field);
            }
        }

        amf3ClassReferences.add(def);
        return def;
    }

    private FieldRef scanForField(Class<?> c, String name) {
        while (c != null) {
            for (Field field: c.getDeclaredFields()) {
                if (field.isAnnotationPresent(NoSerialization.class) || field.isAnnotationPresent(Dynamic.class)) continue;
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) continue;

                if (field.getName().equals(name)) {
                    return new FieldRef(name, name, c);
                }

                if (field.isAnnotationPresent(SerializedName.class)) {
                    if (field.getAnnotation(SerializedName.class).name().equals(name)) {
                        return new FieldRef(field.getName(), name, c);
                    }
                }
            }

            c = c.getSuperclass();
        }

        return null;
    }

    private boolean assertClassMatch(Class<?> c, boolean externalizable, boolean dynamic, String[] members) {
        if (c == null) return false;

        Serialization context = c.getAnnotation(Serialization.class);

        return context.dynamic() == dynamic
                && context.externalizable() == externalizable;
    }

    @AllArgsConstructor
    @ToString
    private class Amf3Header {
        int value;
        boolean isReference;
    }

    @AllArgsConstructor
    private class ClassDef {
        String type;
        String[] members;
        boolean externalizable;
        boolean dynamic;
        Class<?> cls;
    }
    // </editor-fold>
}

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

package net.boreeas.riotapi.rtmp.p2.serialization;

import lombok.AllArgsConstructor;
import lombok.Delegate;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import net.boreeas.riotapi.rtmp.p2.serialization.amf0.Amf0Type;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3ObjectDeserializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3Type;
import org.reflections.Reflections;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created on 5/13/2014.
 */
public class AmfReader {

    @Delegate private DataInputStream in;
    private Reflections reflections;
    private Map<String, Class<?>> serializableClasses;

    private List<Object> amf0ObjectReferences = new ArrayList<>();
    private List<Callable<Object>> amf0Deserializers = new ArrayList<>();

    private List<Object> amf3ObjectReferences = new ArrayList<>();
    private List<String> amf3StringReferences = new ArrayList<>();
    private List<ClassDef> amf3ClassReferences = new ArrayList<>();
    private List<Callable<Object>> amf3Deserializers = new ArrayList<>();

    public AmfReader(InputStream in) {
        this(in, "net.boreeas"); // By default, look for classes from this package
    }

    public AmfReader(InputStream in, String... packages) {
        this.in = new DataInputStream(in);
        this.reflections = new Reflections(packages);
        addDeserializers();
        buildSerializableClassList();
    }

    /**
     * Creates an input stream without rescanning the classpath
     * @param in The inputstream to read from
     * @param original The reader from which to take the set of serializable classes
     */
    public AmfReader(InputStream in, AmfReader base) {
        this.in = new DataInputStream(in);
        this.reflections = base.reflections;
        addDeserializers();
        this.serializableClasses = base.serializableClasses;
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
        amf3Deserializers.add(() -> true);
        amf3Deserializers.add(() -> false);
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

    public void scanPackages(String... packages) {
        reflections.merge(new Reflections(packages));
        buildSerializableClassList();
    }

    private synchronized void buildSerializableClassList() {
        this.serializableClasses = new HashMap<>();
        reflections.getTypesAnnotatedWith(SerializationContext.class).forEach(
                cls -> serializableClasses.put(cls.getAnnotation(SerializationContext.class).traitName(), cls)
        );
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
    public Object decodeAmf0() throws IOException {
        return deserializeAmf0(in.read());
    }

    public <T> T decodeAmf0As() throws IOException {
        return (T) decodeAmf0();
    }

    public Object deserializeAmf0(@NonNull Amf0Type type) throws IOException {
        return deserializeAmf0(type.ordinal());
    }

    private Object deserializeAmf0(int type) throws IOException {
        try {
            return amf0Deserializers.get(type).call();
        } catch (Exception ex) {
            throw new IOException("Error during deserialization", ex);
        }
    }


    // <editor-fold desc="Amf0 Deserializer">
    private Object readAmf0Reference() throws IOException {
        return amf0ObjectReferences.get(in.readShort());
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

    private Map<String, Object> readAmf0KeyValuePairs() throws IOException {
        Map<String, Object> map = new HashMap<>();

        while (true) {
            String name = in.readUTF();
            int type = in.read();
            if (type == Amf0Type.OBJECT_END.ordinal()) { break; }

            map.put(name, deserializeAmf0(type));
        }

        return map;
    }

    public Map<String, Object> readAmf0Map() throws IOException {
        int length = in.readInt();
        Map<String, Object> result = readAmf0KeyValuePairs();
        amf0ObjectReferences.add(result);
        return result;
    }

    @SneakyThrows({IllegalAccessException.class, InstantiationException.class, NoSuchFieldException.class})
    public Object readAmf0Object() throws IOException {
        String name = in.readUTF();
        Map<String, Object> kvPairs = readAmf0KeyValuePairs();

        Class<?> cls = serializableClasses.get(name);
        Object result;

        if (cls == null) {
            result = new AnonymousAmfObject(name, kvPairs);
        } else {
            result = cls.newInstance();
            for (Map.Entry<String, Object> field: kvPairs.entrySet()) {
                Field f = cls.getDeclaredField(field.getKey());
                f.setAccessible(true);

                // amf0 can only encode doubles
                if (f.getType() == Long.class || f.getType() == long.class) {
                    f.set(result, ((Double) field.getValue()).longValue());
                } else if (f.getType() == Integer.class || f.getType() == int.class) {
                    f.set(result, ((Double) field.getValue()).intValue());
                } else if (f.getType() == Short.class || f.getType() == short.class) {
                    f.set(result, ((Double) field.getValue()).shortValue());
                } else if (f.getType() == Byte.class || f.getType() == byte.class) {
                    f.set(result, ((Double) field.getValue()).byteValue());
                } else if (f.getType() == Float.class || f.getType() == float.class) {
                    f.set(result, ((Double) field.getValue()).floatValue());
                } else {
                    f.set(result, field.getValue());
                }

            }
        }

        amf0ObjectReferences.add(result);
        return result;
    }

    public AnonymousAmfObject readAmf0AnonymousObject() throws IOException {
        AnonymousAmfObject obj = new AnonymousAmfObject(in.readUTF(), readAmf0KeyValuePairs());
        amf0ObjectReferences.add(obj);

        return obj;
    }
    // </editor-fold>

    // </editor-fold>

    // <editor-fold desc="Amf3">
    public Object decodeAmf3() throws IOException {
        int type = in.read();
        return deserializeAmf3(type);
    }

    public <T> T decodeAmf3As() throws IOException {
        return (T) decodeAmf3();
    }

    public Object deserializeAmf3(@NonNull Amf3Type type) throws IOException {
        return deserializeAmf3(type.ordinal());
    }

    private Object deserializeAmf3(int type) throws IOException {
        try {
            return amf3Deserializers.get(type).call();
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
        if (total < 128)
            return total;

        total = (total & 0x7f) << 7;
        // second byte
        int nextByte = in.read();
        if (nextByte < 128)
        {
            total = total | nextByte;
        }
        else
        {
            total = (total | nextByte & 0x7f) << 7;
            // third byte
            nextByte = in.read();
            if (nextByte < 128)
            {
                total = total | nextByte;
            }
            else
            {
                total = (total | nextByte & 0x7f) << 8;
                // fourth byte
                nextByte = in.read();
                total = total | nextByte;
            }
        }


        int mask = 1 << 28; // 29th bit holds sign for int29
        return -(total & mask) | total; // -(result & mask) sets all bit above the mask if the bit at the mask is set
    }

    public Date readAmf3Date() throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return (Date) amf3ObjectReferences.get(header.value);

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
        if (header.isReference) return (byte[]) amf3ObjectReferences.get(header.value);

        byte[] buffer = new byte[header.value];
        in.read(buffer);

        amf3ObjectReferences.add(buffer);
        return buffer;
    }

    public Map<Object, Object> readAmf3Array() throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return (Map<Object, Object>) amf3ObjectReferences.get(header.value);

        Map<Object, Object> result = new HashMap<>();
        amf3ObjectReferences.add(result); // Add here for cyclic references

        String key;
        while (!(key = readAmf3String()).isEmpty()) {
            result.put(key, decodeAmf3());
        }

        for (int i = 0; i < header.value; i++) {
            result.put(i, decodeAmf3());
        }

        return result;
    }

    private <T> Object readAmf3Vector(boolean typed, Callable<T> readFunc) throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return amf3ObjectReferences.get(header.value);

        boolean fixedSize = in.readBoolean();
        String type = typed ? readAmf3String() : null;

        List<T> result = new ArrayList<>();
        amf3ObjectReferences.add(result);

        for (int i = 0; i < header.value; i++) {
            try {
                result.add(readFunc.call());
            } catch (Exception e) {
                throw new IOException("Error during deserialization", e);
            }
        }

        return fixedSize ? result.toArray() : result;
    }

    public Object readAmf3VectorInt() throws IOException {
        return readAmf3Vector(false, in::readInt);
    }

    public Object readAmf3VectorUint() throws IOException {
        return readAmf3Vector(false, () -> ((long) in.readInt()) & 0xffffffff);
    }

    public Object readAmf3VectorDouble() throws IOException {
        return readAmf3Vector(false, in::readDouble);
    }

    public Object readAmf3VectorObject() throws IOException {
        return readAmf3Vector(true, this::decodeAmf3);
    }

    public Map<Object, Object> readAmf3Map() throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return (Map<Object, Object>) amf3ObjectReferences.get(header.value);

        boolean weakReferences = in.readBoolean();

        Map<Object, Object> result = new HashMap<>();
        amf3ObjectReferences.add(result);

        for (int i = 0; i < header.value; i++) {
            result.put(decodeAmf3(), decodeAmf3());
        }

        return result;
    }

    @SneakyThrows({IllegalAccessException.class, InstantiationException.class})
    public Object readAmf3Object() throws IOException {
        Amf3Header header = readAmf3Header();
        if (header.isReference) return amf3ObjectReferences.get(header.value);

        ClassDef classDef = readAmf3ClassDefinition(header.value);


        if (classDef.cls == null) { // Read as kv-pairs
            AnonymousAmfObject result = new AnonymousAmfObject(classDef.type, new HashMap<>());
            amf3ObjectReferences.add(result);

            for (String s: classDef.members) {
                result.set(s, decodeAmf3());
            }

            if (classDef.dynamic) {
                String key;
                while (!(key = in.readUTF()).isEmpty()) {
                    result.set(key, decodeAmf3());
                }
            }

            return result;
        } else {
            Object result = classDef.cls.newInstance();
            amf3ObjectReferences.add(result);

            Amf3ObjectDeserializer deserializer = classDef.cls.getAnnotation(SerializationContext.class).deserializer().newInstance();
            deserializer.setReader(this);
            deserializer.deserialize(result, classDef.cls.getAnnotation(SerializationContext.class), in);

            return result;
        }
    }

    private ClassDef readAmf3ClassDefinition(int flags) throws IOException {
        if ((flags & 1) == 0) { // Reference
            return amf3ClassReferences.get(flags >> 1);
        }

        String type = readAmf3String();
        boolean externalizable = ((flags >> 1) & 1) == 1;
        boolean dynamic = ((flags >> 2) & 1) == 1;
        String[] members = new String[flags >> 3];

        for (int i = 0; i < members.length; i++) {
            members[i] = readAmf3String();
        }

        Class match = serializableClasses.get(type);
        ClassDef def;

        if (!assertClassMatch(match, externalizable, dynamic, members)) {
            if (externalizable) {
                // Can't read externalizable objects that don't specify their deserialization process
                throw new IllegalStateException("Undeserializable class " + type);
            }

            def = new ClassDef(type, members, externalizable, dynamic, null);
        } else {
            def = new ClassDef(type, members, externalizable, dynamic, match);
        }

        amf3ClassReferences.add(def);
        return def;
    }

    private boolean assertClassMatch(Class<?> c, boolean externalizable, boolean dynamic, String[] members) {
        if (c == null) return false;

        SerializationContext context = c.getAnnotation(SerializationContext.class);
        boolean membersMatch = true;

        if (context.members().length > 0) {
            membersMatch = Arrays.equals(context.members(), members);
        } else {
            Field[] fields = c.getDeclaredFields();
            Set<String> excludes = new HashSet<>(Arrays.asList(context.excludes()));

            int i = 0;
            for (Field f: fields) {
                if (Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                    continue;
                }

                if (!excludes.contains(f.getName()) && !members[i++].equals(f.getName())) {
                    membersMatch = false;
                    break;
                }
            }
        }

        return context.dynamic() == dynamic
                && context.externalizable() == externalizable
                && membersMatch;
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

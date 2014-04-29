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

import lombok.Delegate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads data from an AMF source
 * Created on 4/15/2014.
 */
@SuppressWarnings("unchecked")
public class AmfInputStream extends InputStream {
    @Delegate
    private InputStream base;
    private List<String> implicitStringTable = new ArrayList<>();
    private List<Object> implicitObjectTable = new ArrayList<>();
    private List<TraitDefinition> traitDefinitionTable = new ArrayList<>();

    public AmfInputStream(InputStream base) {
        this.base = base;
    }

    public DataType readDataType() throws IOException {
        return DataType.values()[read()];
    }

    /**
     * Read an AMF3-encoded integer from the underlying input stream.
     * The high bit of each of the first three byte signify if another byte
     * follows and does not influence the value of the integer.
     * As such, the maximum value of an integer returned this way is
     * 2^29-1
     * @return An integer
     * @throws IOException
     */
    public int readInt() throws IOException {
        int val = 0;
        int next = read();

        if (next >> 7 == 0) {
            return val | (next & 0x7F);
        }

        val = (val & 0x7F) << 7;
        next = read();

        if (next >> 7 == 0) {
            return val | (next & 0x7F);
        }

        val = (val | (next & 0x7F)) << 7;
        next = read();

        if (next >> 7 == 0) {
            return val | (next & 0x7F);
        }

        return ((val | (next & 0x7F)) << 8) | read();
    }

    /**
     * Reads a double from the underlying input stream
     * @return A double
     * @throws IOException
     */
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    private long readLong() throws IOException {
        return (read() << 56) | (read() << 48) | (read() << 40) | (read() << 32)
                | (read() << 24) | (read() << 16) | (read() << 8) | read();
    }

    /**
     * Reads a string from the underlying input stream
     * @return A string
     * @throws IOException
     */
    public String readString() throws IOException {
        return readStringWithTable(implicitStringTable);
    }

    /**
     * Reads a string representing an XML document from the input stream
     * @return A string representing an XML document
     * @throws IOException
     */
    public String readXML() throws IOException {
        return readStringWithTable(implicitObjectTable);
    }

    private String readStringWithTable(List table) throws IOException {
        int length = readInt();

        if (length == 1) {
            return "";
        }

        if ((length & 1) == 1) {
            byte[] data = new byte[length >> 1];
            read(data);

            String value = new String(data, "UTF8");
            table.add(value);

            return value;
        } else {
            return (String) table.get(length >> 1);
        }
    }

    /**
     * Reads a date represented as milliseconds since the unix epoch
     * @return A date
     * @throws IOException
     */
    public Date readDate() throws IOException {
        int marker = readInt();
        if ((marker & 1) == 1) {
            Date date = new Date(readLong());
            implicitObjectTable.add(date);
            return date;
        } else {
            return (Date) implicitObjectTable.get(marker >> 1);
        }
    }

    /**
     * Reads an AMF3-encoded array. These arrays may contain gaps and use
     * Strings as indices, therefore a map is returned
     * @return A map containing the transmitted key/value pairs
     * @throws IOException
     */
    private Map<Object, TypedObject> readArray() throws IOException {
        int length = readInt();

        if ((length & 1) == 1) {
            length >>= 1;
            Map<Object, TypedObject> map = new HashMap<>();

            String key;
            while (!(key = readString()).isEmpty()) {
                map.put(key, decodeObject());
            }

            for (int i = 0; i < length; i++) {
                map.put(i, decodeObject());
            }

            implicitObjectTable.add(map);
            return map;

        } else {
            return (Map) implicitObjectTable.get(length >> 1);
        }
    }

    /**
     * Reads an object from the underlying stream
     * @return An object
     * @throws IOException
     */
    public AmfObject readObject() throws IOException {
        int marker = readInt();
        if ((marker & 1) == 0) {
            return (AmfObject) implicitObjectTable.get(marker >> 1);
        }

        // First get the trait definition

        boolean traitDefinitionCached = (marker & 0b10) == 0;

        TraitDefinition traitDef;
        if (traitDefinitionCached) {
            traitDef = traitDefinitionTable.get(marker >> 2);
        } else {
            boolean externalizable = (marker & 0b100) == 0b100;
            boolean dynamic = (marker & 0b1000) == 0b1000;

            traitDef = new TraitDefinition(readString(), externalizable, dynamic);
            marker >>= 4;

            for (int i = 0; i < marker; i++) {
                traitDef.addMember(readString());
            }

            traitDefinitionTable.add(traitDef);
        }

        // Then create the corresponding object

        AmfObject obj = new AmfObject(traitDef);
        implicitObjectTable.add(obj);

        if (!traitDef.isExternalizable()) {
            for (String field: obj.getMembers()) {
                obj.setField(field, decodeObject());
            }

            if (traitDef.isDynamic()) {
                String key;
                while (!(key = readString()).isEmpty()) {
                    obj.setField(key, decodeObject());
                }
            }
        } else {
            if (ExternalizableObjectReader.defaultInstance().hasReader(obj.getType())) {
                return ExternalizableObjectReader.defaultInstance().readIntoType(this, obj);
            } else {
                throw new RuntimeException("Can't deserialize type " + obj.getType());
            }
        }

        return obj;
    }

    /**
     * Read an array of bytes from the underlying input stream
     * @return A byte array
     * @throws IOException
     */
    public byte[] readByteArray() throws IOException {
        int length = readInt();

        if ((length & 1) == 1) {
            byte[] data = new byte[length >> 1];
            read(data);

            implicitObjectTable.add(data);
            return data;
        } else {
            return (byte[]) implicitObjectTable.get(length >> 2);
        }
    }

    /**
     * Reads a vector of integers from the underlying input stream
     * @return An array of integers representing the vector
     * @throws IOException
     */
    public int[] readIntVector() throws IOException {
        int length = readInt();

        if ((length & 1) == 1) {
            boolean resizable = (read() == 0);  // But we don't actually care

            int[] data = new int[length >> 1];
            for (int i = 0; i < data.length; i++) {
                data[i] = (read() << 24) | (read() << 16) | (read() << 8) | read();
            }

            implicitObjectTable.add(data);
            return data;
        } else {
            return (int[]) implicitObjectTable.get(length >> 1);
        }
    }

    /**
     * Reads a vector of unsigned integers from the underlying input stream
     * @return An array of longs representing the vector
     * @throws IOException
     */
    public long[] readUintVector() throws IOException {
        int length = readInt();

        if ((length & 1) == 1) {
            boolean resizable = (read() == 0);  // But we don't actually care

            long[] data = new long[length >> 1];
            for (int i = 0; i < data.length; i++) {
                data[i] = (read() << 24) | (read() << 16) | (read() << 8) | read();
                data[i] &= 0xffffffffL; // Remove sign bit
            }

            implicitObjectTable.add(data);
            return data;
        } else {
            return (long[]) implicitObjectTable.get(length >> 1);
        }
    }

    /**
     * Reads a vector of doubles from the underlying input stream
     * @return An array of doubles representing the vector
     * @throws IOException
     */
    public double[] readDoubleVector() throws IOException {
        int length = readInt();

        if ((length & 1) == 1) {
            boolean resizable = (read() == 0);  // But we don't actually care

            double[] data = new double[length >> 1];
            for (int i = 0; i < data.length; i++) {
                data[i] = readDouble();
            }

            implicitObjectTable.add(data);
            return data;
        } else {
            return (double[]) implicitObjectTable.get(length >> 1);
        }
    }

    /**
     * Read an array of AMF-encoded objects from the input stream
     * @return An array of typed objects, each representing the type of the object as
     * well as the object itself
     * @throws IOException
     */
    public TypedObject[] readObjectVector() throws IOException {
        int length = readInt();

        if ((length & 1) == 1) {
            boolean resizable = (read() == 0);  // But we don't actually care
            String type = readString();         // Return the objects encoded as TypedObjects instead

            TypedObject[] data = new TypedObject[length >> 1];
            implicitObjectTable.add(data);  // Add here for potential cyclic references

            for (int i = 0; i < data.length; i++) {
                data[i] = decodeObject();
            }

            return data;
        } else {
            return (TypedObject[]) implicitObjectTable.get(length >> 1);
        }
    }

    public Map<TypedObject, TypedObject> readDictionary() throws IOException {
        int length = readInt();

        if ((length & 1) == 0) {
            return (Map<TypedObject, TypedObject>) implicitObjectTable.get(length >> 1);
        }

        boolean weakKeys = (read() == 1);   // We don't care

        Map<TypedObject, TypedObject> result = new HashMap<>();
        implicitObjectTable.add(result);    // Add here for potential cyclic references

        length >>= 1;
        for (int i = 0; i < length; i++) {
            result.put(decodeObject(), decodeObject());
        }

        return result;
    }

    /**
     * Decodes the next AMF3 object from the input stream
     * @return A TypedObject containing the type of the decoded object as well as
     * the decoded object itself
     * @throws IOException
     */
    public TypedObject decodeObject() throws IOException {
        switch (readDataType()) {
            case UNDEFINED: return new TypedObject(DataType.UNDEFINED,  null);
            case NULL:      return new TypedObject(DataType.NULL,       null);
            case TRUE:      return new TypedObject(DataType.TRUE,       true);
            case FALSE:     return new TypedObject(DataType.FALSE,      false);
            case INTEGER:   return new TypedObject(DataType.INTEGER,    readInt());
            case DOUBLE:    return new TypedObject(DataType.DOUBLE,     readDouble());
            case STRING:    return new TypedObject(DataType.STRING,     readString());
            case XML_DOC:   return new TypedObject(DataType.XML_DOC,    readXML());
            case DATE:      return new TypedObject(DataType.DATE,       readDate());
            case ARRAY:     return new TypedObject(DataType.ARRAY,      readArray());
            case OBJECT:    return new TypedObject(DataType.OBJECT,     readObject());
            case XML:       return new TypedObject(DataType.XML,        readXML());
            case BYTE_ARRAY:return new TypedObject(DataType.BYTE_ARRAY, readByteArray());
            case VECTOR_INT:return new TypedObject(DataType.VECTOR_INT, readIntVector());
            case VECTOR_UINT:return new TypedObject(DataType.VECTOR_UINT, readUintVector());
            case VECTOR_DOUBLE:return new TypedObject(DataType.VECTOR_DOUBLE, readDoubleVector());
            case VECTOR_OBJECT:return new TypedObject(DataType.VECTOR_OBJECT, readObjectVector());
            case DICTIONARY:return new TypedObject(DataType.DICTIONARY, readDictionary());
        }

        return null;
    }

    /**
     * A rudimentary implementation of an AMF0-decoder
     * @return The decoded object
     * @throws IOException
     */
    public TypedObject decodeAMF0Object() throws IOException {
        int type = read();
        switch (type) {
            case 0x00: return new TypedObject(DataType.DOUBLE, readAMF0Number());
            case 0x02: return new TypedObject(DataType.STRING, readAMF0String());
            case 0x03: return new TypedObject(DataType.OBJECT, readAMF0Object());
            case 0x05: return new TypedObject(DataType.NULL, null);
            case 0x11: return decodeObject();
        }

        throw new IllegalArgumentException("Unknown AMF0 type " + type);
    }

    public String readAMF0String() throws IOException {
        int length = (read() << 8) | read();
        if (length == 0) {
            return "";
        }

        byte[] data = new byte[length];
        read(data);

        return new String(data, "UTF-8");
    }

    public double readAMF0Number() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public AmfObject readAMF0Object() throws IOException {
        AmfObject obj = new AmfObject(new TraitDefinition("object", true, false));

        String key;
        while (!(key = readAMF0String()).isEmpty()) {
            obj.setField(key, decodeAMF0Object());
        }

        read(); // Object end marker

        return obj;
    }
}

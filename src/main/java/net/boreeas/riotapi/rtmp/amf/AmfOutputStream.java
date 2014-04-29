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
import net.boreeas.riotapi.rtmp.RangeException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 4/16/2014.
 */
public class AmfOutputStream extends OutputStream {
    @Delegate
    private OutputStream base;

    public AmfOutputStream(OutputStream base) {
        this.base = base;
    }

    public void encodeObject(TypedObject object) throws IOException {
        write(object.getType().ordinal());
        switch (object.getType()) {
            case UNDEFINED:
                break;
            case NULL:
                break;
            case TRUE:
                break;
            case FALSE:
                break;
            case INTEGER:
                writeInt(object.getInt());
                break;
            case DOUBLE:
                writeDouble(object.getDouble());
                break;
            case STRING:
                writeString(object.getString());
                break;
            case XML_DOC:
                writeXmlDoc(object.getString());
                break;
            case DATE:
                writeDate(object.getDate());
                break;
            case ARRAY:
                writeArray(object.getArray());
                break;
            case OBJECT:
                writeObject(object.getObject());
                break;
            case XML:
                writeXml(object.getString());
                break;
            case BYTE_ARRAY:
                writeByteArray(object.getByteArray());
                break;
            case VECTOR_INT:
                writeIntVector(object.getIntVector());
                break;
            case VECTOR_UINT:
                writeUintVector(object.getUintVector());
                break;
            case VECTOR_DOUBLE:
                writeDoubleVector(object.getDoubleVector());
                break;
            case VECTOR_OBJECT:
                writeObjectVector(object.getObjectVector());
                break;
            case DICTIONARY:
                writeDictionary(object.getDict());
                break;
        }
    }

    public void writeInt(int val) throws IOException {
        if (val >= 0x4000_0000 || val < 0) {
            throw new RangeException(val);
        }

        int marker = 1 << 7;

        if (val <= 0x7F) {
            write(val);
        } else if (val <= 0x3FFFF) {
            write(val >> 7 | marker);
            write(val);
        } else if (val <= 0x1FFFFF) {
            write(val >> 14 | marker);
            write(val >>  7 | marker);
            write(val);
        } else {
            write(val >> 21 | marker);
            write(val >> 14 | marker);
            write(val >>  7 | marker);
            write(val);
        }
    }

    public void writeDouble(double val) throws IOException {
        writeLong(Double.doubleToLongBits(val));
    }

    public void writeString(String val) throws IOException {
        writeRawString(val);
    }

    public void writeXmlDoc(String val) throws IOException {
        writeRawString(val);
    }

    public void writeXml(String val) throws IOException {
        writeRawString(val);
    }

    private void writeRawString(String val) throws IOException {
        writeInt(val.length() << 1 | 1);
        write(val.getBytes("UTF-8"));
    }

    private void writeLong(long val) throws IOException {
        write((int) (val >> 56));
        write((int) (val >> 48));
        write((int) (val >> 40));
        write((int) (val >> 32));
        write((int) (val >> 24));
        write((int) (val >> 16));
        write((int) (val >>  8));
        write((int) val);
    }

    public void writeDate(Date val) throws IOException {
        write(1);   // not-cached marker
        writeLong(val.getTime());
    }

    public void writeArray(Map<Object, TypedObject> val) throws IOException {

        TypedObject[] ordinalKeys = new TypedObject[val.size()];
        int ordinalSectionLength = 0;

        Map<String, TypedObject> strings = new HashMap<>();

        for (Map.Entry<Object, TypedObject> entry: val.entrySet()) {
            Object key = entry.getKey();
            TypedObject value = entry.getValue();

            if (key instanceof String) {
                strings.put((String) key, value);
            } else {
                ordinalKeys[((int) key)] = value;
                if ((int) key >= ordinalSectionLength) {
                    ordinalSectionLength = (int) key + 1;
                }
            }
        }

        writeInt(ordinalSectionLength << 1 | 1);

        for (Map.Entry<String, TypedObject> entry: strings.entrySet()) {
            writeString(entry.getKey());
            encodeObject(entry.getValue());
        }

        for (int i = 0; i < ordinalSectionLength; i++) {
            encodeObject(ordinalKeys[i]);
        }
    }

    public void writeObject(AmfObject object) throws IOException {
        int marker = 0b11;

        if (object.isExternalizable()) {
            marker |= 0b100;
        }

        if (object.isDynamic()) {
            marker |= 0b1000;
        }

        marker |= (object.getMembers().size() << 4);

        writeInt(marker);
        writeString(object.getType());

        if (object.isExternalizable()) {
            if (ExternalizableObjectWriter.defaultInstance().hasWriter(object.getType())) {
                ExternalizableObjectWriter.defaultInstance().write(this, object);
            } else {
                throw new IllegalArgumentException("Unserializable type: " + object.getType());
            }
        } else {
            for (String member: object.getMembers()) {
                writeString(member);
            }

            for (String field: object.getMembers()) {
                encodeObject(object.getField(field));
            }

            if (object.isDynamic()) {
                writeString("");
            }
        }
    }

    public void writeByteArray(byte[] val) throws IOException {
        writeInt(val.length << 1 | 1);
        write(val);
    }

    public void writeIntVector(int[] val) throws IOException {
        writeInt(val.length << 1 | 1);
        write(1);   // fixed size

        for (int i: val) {
            write(i >> 24);
            write(i >> 16);
            write(i >> 8);
            write(i);
        }
    }

    public void writeUintVector(long[] val) throws IOException {
        writeInt(val.length << 1 | 1);
        write(1);

        for (long i: val) {
            write((int) (i >> 24));
            write((int) (i >> 16));
            write((int) (i >> 8));
            write((int) i);
        }
    }

    public void writeDoubleVector(double[] val) throws IOException {
        writeInt(val.length << 1 | 1);
        write(1);

        for (double d: val) {
            writeLong(Double.doubleToLongBits(d));
        }
    }

    public void writeObjectVector(TypedObject[] vals) throws IOException {
        writeInt(vals.length << 1 | 1);
        write(1);
        writeString("*");   // any type

        for (TypedObject obj: vals) {
            encodeObject(obj);
        }
    }

    public void writeDictionary(Map<TypedObject, TypedObject> map) throws IOException {
        writeInt(map.size() << 1 | 1);
        write(0);   // Strong keys

        for (Map.Entry<TypedObject, TypedObject> entry: map.entrySet()) {
            encodeObject(entry.getKey());
            encodeObject(entry.getValue());
        }
    }

    public void encodeAmf0Object(TypedObject object) throws IOException {
        switch(object.getType()) {
            case INTEGER:
                write(0x00);
                writeAMF0Number(object.getInt());
                break;
            case DOUBLE:
                write(0x00);
                writeAMF0Number(object.getDouble());
                break;
            case TRUE:
                write(0x01);
                write(0x01);
                break;
            case FALSE:
                write(0x01);
                write(0x00);
                break;
            case STRING:
                write(0x02);
                writeAMF0String(object.getString());
                break;
            case OBJECT:
                write(0x03);
                writeAMF0Object(object.getObject());
                break;
            case NULL:
                write(0x05);
                break;
            case AMF3_FROM_AMF0:
                write(0x11);
                encodeObject(TypedObject.fromObject(object.getValue()));
                break;
            default:
                throw new IllegalArgumentException("Unknown AMF0 type " + object.getType());
        }
    }

    public void encodeAmf3ToAmf0(TypedObject object) throws IOException {
        write(0x11);
        encodeObject(object);
    }

    public void writeAMF0Number(double val) throws IOException {
        writeLong(Double.doubleToLongBits(val));
    }

    public void writeAMF0String(String s) throws IOException {
        byte[] data = s.getBytes("UTF-8");

        write(data.length >> 8);
        write(data.length);
        write(data);
    }

    public void writeAMF0Object(AmfObject object) throws IOException {
        for (String key: object.getMembers()) {
            writeAMF0String(key);
            encodeAmf0Object(object.getField(key));
        }

        writeAMF0String("");
        write(0x09);    // Object end
    }
}

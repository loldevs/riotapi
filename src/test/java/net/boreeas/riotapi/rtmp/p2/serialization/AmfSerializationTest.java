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

import junit.framework.TestCase;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.boreeas.riotapi.rtmp.p2.serialization.amf0.Amf0Type;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AmfSerializationTest extends TestCase {
    @AllArgsConstructor private class Container { ByteArrayOutputStream bout; AmfWriter writer;
        ByteArrayOutputStream testBout; DataOutputStream dout; }

    private Container newWriter() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
        return new Container(bout, new AmfWriter(bout), bout2, new DataOutputStream(bout2));
    }

    public void testAmf0WriteNumber() throws IOException {
        Container c = newWriter();
        int i = 0;
        Integer i2 = 0xffff;
        byte b = 1;
        Byte b2 = 3;
        short s = -20;
        Short s2 = -200;
        long l = Long.MAX_VALUE;
        Long l2 = Long.MIN_VALUE;
        double d = 2.7;
        Double d2 = 1.41;
        float f = 3.141f;
        Float f2 = -1.5678f;

        c.writer.serializeAmf0(i);
        c.writer.serializeAmf0(i2);
        c.writer.serializeAmf0(b);
        c.writer.serializeAmf0(b2);
        c.writer.serializeAmf0(s);
        c.writer.serializeAmf0(s2);
        c.writer.serializeAmf0(l);
        c.writer.serializeAmf0(l2);
        c.writer.serializeAmf0(d);
        c.writer.serializeAmf0(d2);
        c.writer.serializeAmf0(f);
        c.writer.serializeAmf0(f2);

        c.dout.writeDouble(i);
        c.dout.writeDouble(i2);
        c.dout.writeDouble(b);
        c.dout.writeDouble(b2);
        c.dout.writeDouble(s);
        c.dout.writeDouble(s2);
        c.dout.writeDouble(l);
        c.dout.writeDouble(l2);
        c.dout.writeDouble(d);
        c.dout.writeDouble(d2);
        c.dout.writeDouble(f);
        c.dout.writeDouble(f2);

        assertTrue(Arrays.equals(c.bout.toByteArray(), c.testBout.toByteArray()));
    }

    public void testAmf0WriteDate() throws IOException {
        Container c = newWriter();

        Date d = new Date();
        c.writer.encodeAmf0(d);

        c.dout.write(Amf0Type.DATE.ordinal());
        c.dout.writeDouble(d.getTime());
        c.dout.writeShort(0);

        assertTrue(Arrays.equals(c.bout.toByteArray(), c.testBout.toByteArray()));
    }

    public void testAmf0WriteString() throws IOException {
        Container c = newWriter();

        c.writer.serializeAmf0("Lorem ipsum dolor sic amet");
        c.dout.writeUTF("Lorem ipsum dolor sic amet");

        assertTrue(Arrays.equals(c.bout.toByteArray(), c.testBout.toByteArray()));
    }

    public void testAmf0WriteMap() throws IOException {
        Container c = newWriter();

        Map<String, String> map = new HashMap<>();
        map.put("test", "blubb");
        map.put("test2", "blah");

        c.writer.encodeAmf0(map);

        c.dout.write(Amf0Type.ECMA_ARRAY.ordinal());
        c.dout.writeInt(map.size());
        for (Map.Entry<String, String> entry: map.entrySet()) {
            c.dout.writeUTF(entry.getKey());
            c.dout.write(Amf0Type.STRING.ordinal());
            c.dout.writeUTF(entry.getValue());
        }

        c.dout.writeShort(0);
        c.dout.write(Amf0Type.OBJECT_END.ordinal());

        assertTrue(Arrays.equals(c.bout.toByteArray(), c.testBout.toByteArray()));
    }

    public void testAmf0WriteArray() throws IOException {
        Container c = newWriter();

        int[] is = new int[]{0, 1, 2, 3, 4};
        String[] strings = new String[] {"Once", "upon", "a", "midnight", "dreary"};

        c.writer.encodeAmf0(is);
        c.writer.encodeAmf0(strings);

        c.dout.write(Amf0Type.STRICT_ARRAY.ordinal());
        c.dout.writeInt(is.length);
        for (int i: is) {
            c.dout.write(Amf0Type.NUMBER.ordinal());
            c.dout.writeDouble(i);
        }

        c.dout.write(Amf0Type.STRICT_ARRAY.ordinal());
        c.dout.writeInt(strings.length);
        for (String string: strings) {
            c.dout.write(Amf0Type.STRING.ordinal());
            c.dout.writeUTF(string);
        }

        assertTrue(Arrays.equals(c.bout.toByteArray(), c.testBout.toByteArray()));
    }

    public void testAmf0WriteBoolean() throws IOException {
        Container c = newWriter();

        c.writer.serializeAmf0(true);
        c.writer.serializeAmf0(false);

        c.dout.write(1);
        c.dout.write(0);

        assertTrue(Arrays.equals(c.bout.toByteArray(), c.testBout.toByteArray()));
    }

    public void testAmf3SerializeInt29() throws IOException {
        Container container = newWriter();
        container.writer.serializeAmf3(1);
        container.writer.serializeAmf3(0x7e);
        container.writer.serializeAmf3(0x7f);
        container.writer.serializeAmf3(0xffff);
        container.writer.serializeAmf3(0x1289ef);

        container.testBout.write(0x1);
        container.testBout.write(0x7e);
        container.testBout.write(0x7f);
        container.testBout.write(0x83);container.testBout.write(0xff);container.testBout.write(0x7f);
        container.testBout.write(0xca);container.testBout.write(0x93);container.testBout.write(0x6f);

        byte[] original = container.bout.toByteArray();
        byte[] target = container.testBout.toByteArray();

        assertTrue("Original and target array not equal", Arrays.equals(original, target));

        AmfReader reader = new AmfReader(new ByteArrayInputStream(original));
        assertEquals("Failed at 0x01", 1, reader.readInt29());
        assertEquals("Failed at 0x7e", 0x7e, reader.readInt29());
        assertEquals("Failed at 0x7f", 0x7f, reader.readInt29());
        assertEquals("Failed at 0xffff", 0xffff, reader.readInt29());
        assertEquals("Failed at 0x1289ef", 0x1289ef, reader.readInt29());
    }

    public void testAmf0ObjectSerializationDeserialization() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AmfWriter writer = new AmfWriter(out);

        SerializationTestObject original = new SerializationTestObject();
        original.i = 1234567;
        original.j = -1;
        original.created = new Date();
        original.name = "Test";

        writer.encodeAmf0(original);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        AmfReader reader = new AmfReader(in);

        SerializationTestObject copy = (SerializationTestObject) reader.decodeAmf0();
        assertEquals(original, copy);
    }

    public void testAmf3ObjectSerializationDeserialization() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AmfWriter writer = new AmfWriter(out);

        SerializationTestObject original = new SerializationTestObject();
        original.i = 1234567;
        original.j = -1;
        original.created = new Date();
        original.name = "Test";

        SerializationTestObject original2 = new SerializationTestObject();
        original2.i = 3141;
        original2.j = 271;
        original2.created = new Date();
        original2.name = "Also Test";

        writer.encodeAmf3(original);
        writer.encodeAmf3(original2);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        AmfReader reader = new AmfReader(in);

        Object obj = reader.decodeAmf3();
        SerializationTestObject copy = (SerializationTestObject) obj;
        SerializationTestObject copy2 = (SerializationTestObject) reader.decodeAmf3();

        assertEquals(original, copy);
        assertEquals(original2, copy2);
    }


    @EqualsAndHashCode
    @SerializationContext(traitName = "net.boreeas.test.TestObject", excludes = {"this$0"})
    @NoArgsConstructor
    @ToString
    public static class SerializationTestObject {
        private static final String blah = "blah";
        private int i;
        private int j;
        private String name;
        private Date created;
    }
}
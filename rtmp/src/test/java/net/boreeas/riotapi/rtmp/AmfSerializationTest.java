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

package net.boreeas.riotapi.rtmp;

import junit.framework.TestCase;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.boreeas.riotapi.Util;
import net.boreeas.riotapi.rtmp.serialization.AmfReader;
import net.boreeas.riotapi.rtmp.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AmfSerializationTest extends TestCase {
    @AllArgsConstructor private class Container { ByteArrayOutputStream bout; AmfWriter writer;
        ByteArrayOutputStream testBout; DataOutputStream dout; }

    private Container newWriter() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
        return new Container(bout, new AmfWriter(bout), bout2, new DataOutputStream(bout2));
    }

    private byte[] expect(int[] values) {
        byte[] res = new byte[values.length];
        for (int i = 0;i  < res.length;i++) {
            res[i] = (byte) values[i];
        }

        return res;
    }

    public void testAmf0Primitives() throws IOException {
        Container c = newWriter();
        AmfWriter w = c.writer;
        w.encodeAmf0(0);
        w.encodeAmf0(12345.0);
        w.encodeAmf0(true);
        w.encodeAmf0(false);
        w.encodeAmf0("Test");
        w.encodeAmf0(null);
        Object[] o = new Object[]{1, 2, "false"};
        w.encodeAmf0(o);
        w.encodeAmf0(new Date(0));
        w.encodeAmf0(new Date(0));


        // From astralfoxy's rtmp-sharp, adjusted for bugs in the date serialization code
        byte[] expected = expect(new int[] {
                0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x40,0xC8,0x1C,0x80,0x0,0x0,
                0x0,0x0,0x1,0x1,0x1,0x0,0x2,0x0,0x4,0x54,0x65,0x73,0x74,0x5,0xA,0x0,
                0x0,0x0,0x3,0x0,0x3F,0xF0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x40,0x0,0x0,
                0x0,0x0,0x0,0x0,0x0,0x2,0x0,0x5,0x66,0x61,0x6C,0x73,0x65,0xB,0x0,0x0,
                0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xB,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
                0x0,0x0, 0x0
        });

        assertTrue("\nExpected " + Arrays.toString(expected) + "\n" +
                        "     Got " + Arrays.toString(c.bout.toByteArray()),
                Arrays.equals(c.bout.toByteArray(), expected));

        AmfReader reader = new AmfReader(new ByteArrayInputStream(expected));
        assertEquals("expected 0", 0.0, reader.decodeAmf0());
        assertEquals("expected 12345.0", 12345.0, reader.decodeAmf0());
        assertTrue("expected true", reader.decodeAmf0());
        assertFalse("expected false", reader.decodeAmf0());
        assertEquals("expected 'Test'", "Test", reader.decodeAmf0());
        assertEquals("expected null", null, reader.decodeAmf0());
        assertTrue("expecte {1,2,'false'", Arrays.equals(new Object[]{1.0, 2.0, "false"}, reader.decodeAmf0()));
        assertEquals("expected epoch", new Date(0), reader.decodeAmf0());
        assertEquals("expected epoch", new Date(0), reader.decodeAmf0());
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

        c.writer.encodeAmf0(i);
        c.writer.encodeAmf0(i2);
        c.writer.encodeAmf0(b);
        c.writer.encodeAmf0(b2);
        c.writer.encodeAmf0(s);
        c.writer.encodeAmf0(s2);
        c.writer.encodeAmf0(l);
        c.writer.encodeAmf0(l2);
        c.writer.encodeAmf0(d);
        c.writer.encodeAmf0(d2);
        c.writer.encodeAmf0(f);
        c.writer.encodeAmf0(f2);

        // From astralfoxy's rtmp-sharp
        byte[] expected = expect(new int[] {
                0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x40,0xEF,0xFF,0xE0,0x0,0x0,
                0x0,0x0,0x0,0x3F,0xF0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x40,0x8,0x0,0x0,
                0x0,0x0,0x0,0x0,0x0,0xC0,0x34,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xC0,0x69,
                0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x43,0xE0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
                0xC3,0xE0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x40,0x5,0x99,0x99,0x99,0x99,0x99,

                0x9A,0x0,0x3F,0xf6,0x8F,0x5C,0x28,0xF5,0xC2,0x8F,0x0,0x40,0x9,0x20,0xC4,
                0xA0,
                0x0,0x0,0x0,0x0,0xBF,0xF9,0x15,0xB5,0x80,0x0,0x0,0x0
        });


        assertTrue("\nExpected " + Arrays.toString(expected) + "\n" +
                        "     Got " + Arrays.toString(c.bout.toByteArray()),
                Arrays.equals(c.bout.toByteArray(), expected));

        AmfReader reader = new AmfReader(new ByteArrayInputStream(expected));
        assertEquals("i1 wrong", (double) reader.decodeAmf0(), (double) i);
        assertEquals("i2 wrong", reader.decodeAmf0(), (double) i2);
        assertEquals("b wrong", (double) reader.decodeAmf0(), (double) b);
        assertEquals("b2 wrong", reader.decodeAmf0(), (double) b2);
        assertEquals("s wrong", (double) reader.decodeAmf0(), (double) s);
        assertEquals("s2 wrong", reader.decodeAmf0(), (double) s2);
        assertEquals("l wrong", (double) reader.decodeAmf0(), (double) l);
        assertEquals("l2 wrong", reader.decodeAmf0(), (double) l2);
        assertEquals("d wrong", (double) reader.decodeAmf0(), (double) d);
        assertEquals("d2 wrong", reader.decodeAmf0(), (double) d2);
        assertEquals("f wrong", (double) reader.decodeAmf0(), (double) f);
        assertEquals("f2 wrong", reader.decodeAmf0(), (double) f2);
    }


    public void testAmf3SerializeInt29() throws IOException {
        Container c = newWriter();
        c.writer.encodeAmf3(1);
        c.writer.encodeAmf3(0x7e);
        c.writer.encodeAmf3(0x7f);
        c.writer.encodeAmf3(0xffff);
        c.writer.encodeAmf3(0x1289ef);
        c.writer.encodeAmf3(0xffffff);

        // From astralfoxy's rtmp-sharp
        byte[] expected = expect(new int[] {
                0x4,0x1,0x4,0x7E,0x4,0x7F,0x4,0x83,0xFF,0x7F,0x4,0xCA,0x93,0x6F,0x4,0x83
                ,
                0xFF,0xFF,0xFF});

        assertTrue("\nExpected " + Arrays.toString(expected) + "\n" +
                        "     Got " + Arrays.toString(c.bout.toByteArray()),
                Arrays.equals(c.bout.toByteArray(), expected));

        AmfReader reader = new AmfReader(new ByteArrayInputStream(expected));
        assertEquals("Failed at 0x01", 1, (int) reader.decodeAmf3());
        assertEquals("Failed at 0x7e", 0x7e, (int) reader.decodeAmf3());
        assertEquals("Failed at 0x7f", 0x7f, (int) reader.decodeAmf3());
        assertEquals("Failed at 0xffff", 0xffff, (int) reader.decodeAmf3());
        assertEquals("Failed at 0x1289ef", 0x1289ef, (int) reader.decodeAmf3());
        assertEquals("Failed at 0x1289ef", 0xffffff, (int) reader.decodeAmf3());
    }

    public void testAmf3Primitives() throws IOException {
        Container c = newWriter();
        AmfWriter w = c.writer;
        w.encodeAmf3(null);
        w.encodeAmf3(true);
        w.encodeAmf3(false);
        w.encodeAmf3(0);
        w.encodeAmf3(0xffffff);
        w.encodeAmf3(12345.0);
        w.encodeAmf3("Test");
        w.encodeAmf3("Test");
        w.encodeAmf3(new Date(0));
        w.encodeAmf3(new Date(0));
        w.encodeAmf3(new byte[]{1,2,3,4});
        w.encodeAmf3(new int[] {1, 200, 300000, 400000000});
        w.encodeAmf3(new double[] {-3.0, -0.0,5000134.34234 });

        // From astralfoxy's rtmp-sharp, compensating for bugs with date (0 should be serialized as 0, no references)
        // and vector serialization (always uses int vector marking)
        byte[] expected = expect(new int[] {
                0x1,0x3,0x2,0x4,0x0,0x4,0x83,0xFF,0xFF,0xFF,0x5,0x40,0xC8,0x1C,0x80,0x0,

                0x0,0x0,0x0,0x6,0x9,0x54,0x65,0x73,0x74,0x6,0x0,0x8,0x1,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x0,0x8,0x0,0xC,
                0x9,0x1,0x2,0x3,0x4,0xD,0x9,0x1,0x0,0x0,0x0,0x1,0x0,0x0,0x0,0xC8,
                0x0,0x4,0x93,0xE0,0x17,0xD7,0x84,0x0,0xF,0x7,0x1,0xC0,0x8,0x0,0x0,0x0,
                0x0,0x0,0x0,0x80,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x41,0x53,0x12,0xF1,0x95,
                0xE8,0xE6,0x8});

        assertTrue("\nExpected " + Arrays.toString(expected) + "\n" +
                        "     Got " + Arrays.toString(c.bout.toByteArray()),
                Arrays.equals(c.bout.toByteArray(), expected));

        AmfReader reader = new AmfReader(new ByteArrayInputStream(expected));
        assertEquals("Not null", reader.decodeAmf3(), null);
        boolean val = reader.decodeAmf3();
        assertTrue("Not true: " + val, val);
        val = reader.decodeAmf3();
        assertFalse("Not false: " + val, val);
        assertEquals("Not 0", (int) reader.decodeAmf3(), 0);
        assertEquals("Not 0xffffff", (int) reader.decodeAmf3(), 0xffffff);
        assertEquals("Not 12345.0", reader.decodeAmf3(), 12345.0);
        String a = reader.decodeAmf3();
        String b = reader.decodeAmf3();
        assertEquals("Not 'Test'", a, "Test");
        assertTrue("Not rereferenced", a == b);
        assertEquals("Not epoch time", reader.decodeAmf3(), new Date(0));
        assertEquals("Not epoch time/2", reader.decodeAmf3(), new Date(0));
        byte[] arr = reader.decodeAmf3();
        List<Integer> ivec = reader.decodeAmf3();
        List<Double> dvec = reader.decodeAmf3();

       assertTrue("Expected {1,2,3,4}, got " + Arrays.toString(arr), Arrays.equals(arr, new byte[]{1, 2, 3, 4}));
       assertEquals("Expected {1, 200, 300000, 400000000}, got " + ivec, ivec, Arrays.asList(1, 200, 300000, 400000000));
       assertEquals("Expected {-3.0, -0.0,5000134.34234 }, got " + dvec, dvec, Arrays.asList(-3.0, -0.0, 5000134.34234));
    }

    public void testAmf0ObjectSerializationDeserialization() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AmfWriter writer = new AmfWriter(out);

        Champion op = new Champion();
        op.name = "Win Nhao";
        op.id = 9999;
        op.ad = 114.32;
        op.skills = new Skill[]{new Skill("Instagib", 9999), new Skill("Drophack", 0), new Skill("Destroy Nexus", Integer.MAX_VALUE)};

        writer.encodeAmf0(op);

        byte[] expected = expect(new int[] {
                0x10,0x0,0x9,0x44,0x62,0x67,0x2E,0x43,0x68,0x61,0x6D,0x70,0x0,0x4,0x6E,0x61,
                0x6D,0x65,0x2,0x0,0x8,0x57,0x69,0x6E,0x20,0x4E,0x68,0x61,0x6F,0x0,0x2,0x69,
                0x64,0x0,0x40,0xC3,0x87,0x80,0x0,0x0,0x0,0x0,0x0,0x2,0x61,0x64,0x0,0x40,
                0x5C,0x94,0x7A,0xE1,0x47,0xAE,0x14,0x0,0x6,0x73,0x6B,0x69,0x6C,0x6C,0x73,0xA,
                0x0,0x0,0x0,0x3,0x10,0x0,0x9,0x44,0x62,0x67,0x2E,0x53,0x6B,0x69,0x6C,0x6C,
                0x0,0x4,0x6E,0x61,0x6D,0x65,0x2,0x0,0x8,0x49,0x6E,0x73,0x74,0x61,0x67,0x69,
                0x62,0x0,0x6,0x64,0x61,0x6D,0x61,0x67,0x65,0x0,0x40,0xC3,0x87,0x80,0x0,0x0,
                0x0,0x0,0x0,0x0,0x9,0x10,0x0,0x9,0x44,0x62,0x67,0x2E,0x53,0x6B,0x69,0x6C,
                0x6C,0x0,0x4,0x6E,0x61,0x6D,0x65,0x2,0x0,0x8,0x44,0x72,0x6F,0x70,0x68,0x61,
                0x63,0x6B,0x0,0x6,0x64,0x61,0x6D,0x61,0x67,0x65,0x0,0x0,0x0,0x0,0x0,0x0,
                0x0,0x0,0x0,0x0,0x0,0x9,0x10,0x0,0x9,0x44,0x62,0x67,0x2E,0x53,0x6B,0x69,
                0x6C,0x6C,0x0,0x4,0x6E,0x61,0x6D,0x65,0x2,0x0,0xD,0x44,0x65,0x73,0x74,0x72,
                0x6F,0x79,0x20,0x4E,0x65,0x78,0x75,0x73,0x0,0x6,0x64,0x61,0x6D,0x61,0x67,0x65,
                0x0,0x41,0xDF,0xFF,0xFF,0xFF,0xC0,0x0,0x0,0x0,0x0,0x9,0x0,0x0,0x9,0x0,
        });

        AmfReader reader = new AmfReader(new ByteArrayInputStream(expected));
        Champion champ = reader.decodeAmf0();
        assertEquals("original != expected", op, champ);

        reader = new AmfReader(new ByteArrayInputStream(out.toByteArray()));
        Champion c2 = reader.decodeAmf0();
        assertEquals("original != written", op, c2);

        assertEquals("written != expected", champ, c2);
    }

    public void testAmf3ObjectSerializationDeserialization() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AmfWriter writer = new AmfWriter(out);

        Champion op = new Champion();
        op.name = "Win Nhao";
        op.id = 9999;
        op.ad = 114.32;
        op.skills = new Skill[]{new Skill("Instagib", 9999), new Skill("Drophack", 0), new Skill("Destroy Nexus", -0xffffff)};

        writer.encodeAmf3(op);

        byte[] expected = expect(new int[] {
                        0xA,0x43,0x13,0x44,0x62,0x67,0x2E,0x43,0x68,0x61,0x6D,0x70,0x9,0x6E,0x61
                        ,0x6D,
                        0x65,0x5,0x69,0x64,0x5,0x61,0x64,0xD,0x73,0x6B,0x69,0x6C,0x6C,0x73,0x6,0x11,
                        0x57,0x69,0x6E,0x20,0x4E,0x68,0x61,0x6F,0x4,0xCE,0xF,0x5,0x40,0x5C,0x94,
                        0x7A,
                        0xE1,0x47,0xAE,0x14,0x9,0x7,0x1,0xA,0x23,0x13,0x44,0x62,0x67,0x2E,0x53,0x6B,
                        0x69,0x6C,0x6C,0x2,0xD,0x64,0x61,0x6D,0x61,0x67,0x65,0x6,0x11,0x49,0x6E,
                        0x73,
                        0x74,0x61,0x67,0x69,0x62,0x4,0xCE,0xF,0xA,0x23,0xC,0x2,0xE,0x6,0x11,0x44
                        ,
                        0x72,0x6F,0x70,0x68,0x61,0x63,0x6B,0x4,0x0,0xA,0x23,0xC,0x2,0xE,0x6,0x1B
                        ,
                        0x44,0x65,0x73,0x74,0x72,0x6F,0x79,0x20,0x4E,0x65,0x78,0x75,0x73,0x4,0xFC,0x80,
                        0x80,0x1,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
                        0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
                        0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
                        0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
                        0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
                        0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
                        0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
                        0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
        });

        System.out.println("\nExpected:");
        Util.hexdump(expected).forEach(System.out::println);

        System.out.println("\nCreated:");
        Util.hexdump(out.toByteArray()).forEach(System.out::println);

        AmfReader reader = new AmfReader(new ByteArrayInputStream(expected));
        Champion champ = reader.decodeAmf3();
        assertEquals("original != expected", op, champ);

        reader = new AmfReader(new ByteArrayInputStream(out.toByteArray()));
        Champion c2 = reader.decodeAmf3();
        assertEquals("original != written", op, c2);

        assertEquals("written != expected", champ, c2);

    }

    @ToString
    @EqualsAndHashCode
    @Serialization(name = "Dbg.Champ")
    public static class Champion {
        int id;
        double ad;
        String name;
        Skill[] skills;
    }

    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    @Serialization(name="Skill", noncanonicalNames = "Dbg.Skill")
    public static class Skill {
        private String name;
        private int damage;
    }
}
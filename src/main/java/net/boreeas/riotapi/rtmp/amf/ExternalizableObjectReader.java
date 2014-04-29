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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 4/16/2014.
 */
public class ExternalizableObjectReader {
    private static ExternalizableObjectReader DEFAULT_INSTANCE;

    private Map<String, ExtObjReader> readers = new HashMap<>();

    public void registerReader(String type, ExtObjReader reader) {
        readers.put(type, reader);
    }

    public AmfObject readIntoType(AmfInputStream stream, AmfObject obj) throws IOException {
        readers.get(obj.getType()).read(stream, obj);
        return obj;
    }

    public boolean hasReader(String type) {
        return readers.containsKey(type);
    }

    public static ExternalizableObjectReader defaultInstance() {
        if (DEFAULT_INSTANCE == null) {
            DEFAULT_INSTANCE = new ExternalizableObjectReader();
            DEFAULT_INSTANCE.registerReader("DSA", DSAReader::readDSA);
            DEFAULT_INSTANCE.registerReader("DSK", DSKReader::readDSK);
            DEFAULT_INSTANCE.registerReader("flex.messaging.io.ArrayCollection", ArrayCollectionReader::readArrayCollection);
            DEFAULT_INSTANCE.registerReader("com.riotgames.platform.systemstate.ClientSystemStatesNotification", RiotNotificationReader::readNotification);
            DEFAULT_INSTANCE.registerReader("com.riotgames.platform.broadcast.BroadcastNotification", RiotNotificationReader::readNotification);
        }

        return DEFAULT_INSTANCE;
    }








    /*
        Default instance readers follow
        Thanks to gvaneyck for these
     */


    public static class DSAReader {
        public static void readDSA(AmfInputStream in, AmfObject obj) throws IOException {

            List<Integer> flags = readFlags(in);
            for (int i = 0; i < flags.size(); i++) {
                int flag = flags.get(i);
                int bits = 8;

                if (i == 0) {
                    bits = 7;
                    if ((flag & 0x01) == 0x01) {
                        obj.setField("body", in.decodeObject());
                    }
                    if ((flag & 0x02) == 0x02) {
                        obj.setField("clientId", in.decodeObject());
                    }
                    if ((flag & 0x04) == 0x04) {
                        obj.setField("destination", in.decodeObject());
                    }
                    if ((flag & 0x08) == 0x08) {
                        obj.setField("headers", in.decodeObject());
                    }
                    if ((flag & 0x10) == 0x10) {
                        obj.setField("messageId", in.decodeObject());
                    }
                    if ((flag & 0x20) == 0x20) {
                        obj.setField("timeStamp", in.decodeObject());
                    }
                    if ((flag & 0x40) == 0x40) {
                        obj.setField("timeToLive", in.decodeObject());
                    }
                } else if (i == 1) {
                    bits = 2;

                    if ((flag & 0x01) == 0x01) {
                        TypedObject bytes = in.decodeObject();
                        TypedObject asString = new TypedObject(DataType.STRING, byteArrayToId((byte[]) bytes.getValue()));

                        obj.setField("clientIdBytes", bytes);
                        obj.setField("clientId", asString);
                    }
                    if ((flag & 0x02) == 0x02) {
                        TypedObject bytes = in.decodeObject();
                        TypedObject asString = new TypedObject(DataType.STRING, byteArrayToId((byte[]) bytes.getValue()));

                        obj.setField("messageIdBytes", bytes);
                        obj.setField("messageId", asString);
                    }
                }

                // Potentially additional objects follow, so decode from input stream
                // until no flags are left
                readRemaining(flag, bits, in);
            }

            // And another set of flags
            flags = readFlags(in);
            for (int i = 0; i < flags.size(); i++) {
                int flag = flags.get(i);
                int bits = 0;

                if (i == 0) {
                    bits = 2;
                    if ((flag & 0x01) == 0x01) {
                        obj.setField("correlationId", in.decodeObject());
                    }
                    if ((flag & 0x02) == 0x02) {
                        TypedObject bytes = in.decodeObject();
                        TypedObject asString = new TypedObject(DataType.STRING, byteArrayToId((byte[]) bytes.getValue()));

                        obj.setField("correlationIdBytes", bytes);
                        obj.setField("correlationId", asString);
                    }
                }

                readRemaining(flag, bits, in);
            }
        }
    }

    public static class DSKReader {
        public static void readDSK(AmfInputStream in, AmfObject obj) throws IOException {
            DSAReader.readDSA(in, obj);

            for (int flag: readFlags(in)) {
                readRemaining(flag, 0, in);
            }
        }
    }

    public static class ArrayCollectionReader {
        public static void readArrayCollection(AmfInputStream in, AmfObject obj) throws IOException {
            obj.setField("array", in.decodeObject());
        }
    }

    public static class RiotNotificationReader {
        public static void readNotification(AmfInputStream in, AmfObject obj) throws IOException {

            int size = (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
            byte[] chars = new byte[size];
            in.read(chars);

            JsonElement json = new Gson().toJsonTree(new String(chars, "UTF-8"));
            json.getAsJsonObject().entrySet().forEach(entry -> obj.setField(entry.getKey(), objFromJson(entry.getValue())));
        }

        private static TypedObject objFromJson(JsonElement json) {

            if (json.isJsonObject()) {
                AmfObject obj = new AmfObject(new TraitDefinition("object", true, false));
                json.getAsJsonObject().entrySet().forEach(entry -> obj.setField(entry.getKey(), objFromJson(entry.getValue())));

                return new TypedObject(DataType.OBJECT, obj);
            }

            if (json.isJsonArray()) {
                Map<Object, TypedObject> array = new HashMap<>();
                for (int i = 0; i < json.getAsJsonArray().size(); i++) {
                    array.put(i, objFromJson(json.getAsJsonArray().get(i)));
                }

                return new TypedObject(DataType.ARRAY, array);
            }

            if (json.isJsonNull()) {
                return new TypedObject(DataType.NULL, null);
            }

            if (json.isJsonPrimitive()) {
                JsonPrimitive p = json.getAsJsonPrimitive();

                if (p.isBoolean()) {
                    return p.getAsBoolean() ? new TypedObject(DataType.TRUE, true) : new TypedObject(DataType.FALSE, false);
                }

                if (p.isNumber()) {
                    if (p.getAsNumber().doubleValue() == p.getAsNumber().intValue()) { // integer
                        return new TypedObject(DataType.INTEGER, p.getAsInt());
                    } else {
                        return new TypedObject(DataType.DOUBLE, p.getAsDouble());
                    }
                }

                if (p.isString()) {
                    return new TypedObject(DataType.STRING, p.getAsString());
                }
            }

            throw new IllegalArgumentException("Unknown JSON type " + json);
        }

    }



    /**
     * Decode the remaining objects marked by the flag, without using them
     * @param flag The flag marking the objects
     * @param skip The amount of bits that were already used
     * @param in The input stream to read from
     * @throws IOException
     */
    private static void readRemaining(int flag, int skip, AmfInputStream in) throws IOException {
        if ((flag >>= skip) != 0) {
            while (flag != 0) {
                in.decodeObject();
                flag >>= 1;
            }
        }
    }


    /**
     * Read flag bytes until the high byte is set
     * @param in Inputstream to read from
     * @return A list of flags
     * @throws IOException
     */
    private static List<Integer> readFlags(AmfInputStream in) throws IOException {
        List<Integer> flags = new ArrayList<Integer>();
        int flag;
        do {
            flag = in.read();
            flags.add(flag);
        } while ((flag & 0x80) != 0);

        return flags;
    }

    /**
     * Converts an array of bytes into an ID string
     *
     * @return The ID string
     */
    private static String byteArrayToId(byte[] data)
    {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < data.length; i++)
        {
            if (i == 4 || i == 6 || i == 8 || i == 10)
                ret.append('-');
            ret.append(String.format("%02x", data[i]));
        }

        return ret.toString();
    }
}

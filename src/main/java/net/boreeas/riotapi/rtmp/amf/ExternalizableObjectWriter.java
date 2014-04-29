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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 4/16/2014.
 */
public class ExternalizableObjectWriter {
    private static ExternalizableObjectWriter DEFAULT_INSTANCE;

    private Map<String, ExtObjWriter> writers = new HashMap<>();

    public void registerWriter(String type, ExtObjWriter reader) {
        writers.put(type, reader);
    }

    public void write(AmfOutputStream stream, AmfObject obj) throws IOException {
        writers.get(obj.getType()).write(stream, obj);
    }

    public boolean hasWriter(String type) {
        return writers.containsKey(type);
    }

    public static ExternalizableObjectWriter defaultInstance() {
        if (DEFAULT_INSTANCE == null) {
            DEFAULT_INSTANCE = new ExternalizableObjectWriter();
    /* As long as we don't need them...
            DEFAULT_INSTANCE.registerWriter("DSA", DSAWriter::readDSA);
            DEFAULT_INSTANCE.registerWriter("DSK", DSKWriter::readDSK);
            DEFAULT_INSTANCE.registerWriter("com.riotgames.platform.systemstate.ClientSystemStatesNotification", RiotNotificationWriter::readNotification);
            DEFAULT_INSTANCE.registerWriter("com.riotgames.platform.broadcast.BroadcastNotification", RiotNotificationWriter::readNotification);
    */
            DEFAULT_INSTANCE.registerWriter("flex.messaging.io.ArrayCollection", ArrayCollectionWriter::writeArrayCollection);
        }

        return DEFAULT_INSTANCE;
    }

    public static class ArrayCollectionWriter {
        public static void writeArrayCollection(AmfOutputStream out, AmfObject obj) throws IOException {
            out.encodeObject(obj.getField("array"));
        }
    }
}

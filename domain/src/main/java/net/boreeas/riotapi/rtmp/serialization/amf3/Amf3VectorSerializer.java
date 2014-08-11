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

package net.boreeas.riotapi.rtmp.serialization.amf3;

import net.boreeas.riotapi.rtmp.serialization.AmfSerializer;
import net.boreeas.riotapi.rtmp.serialization.AmfWriter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created on 5/15/2014.
 */
public class Amf3VectorSerializer implements AmfSerializer<List> {

    private AmfWriter writer;

    public Amf3VectorSerializer(AmfWriter writer) {
        this.writer = writer;
    }

    @Override
    public void serialize(List list, DataOutputStream out) throws IOException {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Can't infer type of empty vector");
        }

        writer.serializeAmf3(list.size() << 1 | 1);
        out.write(0);   // Size not fixed

        Class c = list.iterator().next().getClass();

        if (c == Integer.class) {
            serializeInts(list, new DataOutputStream(out));
        } else if (c == Long.class) {
            serializeUints(list, new DataOutputStream(out));
        } else if (c == Double.class) {
            serializeDoubles(list, new DataOutputStream(out));
        } else {
            serializeObjects(list, new DataOutputStream(out));
        }
    }

    private void serializeInts(List list, DataOutputStream out) throws IOException {
        for (Object object: list) {
            out.writeInt((Integer) object);
        }
    }

    private void serializeUints(List list, DataOutputStream out) throws IOException {
        for (Object object: list) {
            out.writeInt(((Long) object).intValue());
        }
    }

    private void serializeDoubles(List list, DataOutputStream out) throws IOException {
        for (Object object: list) {
            out.writeDouble((Double) object);
        }
    }

    private void serializeObjects(List list, DataOutputStream out) throws IOException {
        writer.serializeAmf3("*");
        for (Object object: list) {
            writer.encodeAmf3(object);
        }
    }
}

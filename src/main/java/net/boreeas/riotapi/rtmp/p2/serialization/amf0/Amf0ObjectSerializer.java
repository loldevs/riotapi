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

package net.boreeas.riotapi.rtmp.p2.serialization.amf0;

import lombok.Setter;
import lombok.SneakyThrows;
import net.boreeas.riotapi.rtmp.p2.serialization.AmfSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.p2.serialization.SerializationContext;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created on 5/8/2014.
 */
public class Amf0ObjectSerializer implements AmfSerializer {

    @Setter protected AmfWriter writer;
    @Setter protected SerializationContext context;

    @Override
    public void serialize(Object obj, DataOutputStream out) throws IOException {

        if (context.traitName().isEmpty()) {
            serializeAnonymous(obj, out);
        } else {
            serializeTyped(obj, out);
        }
    }

    @SneakyThrows({NoSuchFieldException.class, IllegalAccessException.class})
    public void serializeTyped(Object obj, OutputStream out) throws IOException {
        DataOutputStream dout  = new DataOutputStream(out);
        dout.writeUTF(context.traitName());

        if (context.members().length == 0) {
            serializeAnonymous(obj, out);
            return;
        }

        for (String s: context.members()) {
            dout.writeUTF(s);

            Field f = obj.getClass().getDeclaredField(s);
            f.setAccessible(true);
            writer.encodeAmf0(f.get(obj));
        }

        dout.writeUTF("");
        dout.write(Amf0Type.OBJECT_END.ordinal());
    }

    @SneakyThrows(value = {IllegalAccessException.class})
    public void serializeAnonymous(Object obj, OutputStream out) throws IOException {
        DataOutputStream dout = new DataOutputStream(out);
        Set<String> excludes = new HashSet<>(Arrays.asList(context.excludes()));

        for (Field f: obj.getClass().getDeclaredFields()) {
            if (excludes.contains(f.getName()) || Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                continue;
            }

            f.setAccessible(true);
            dout.writeUTF(f.getName());
            writer.encodeAmf0(f.get(obj));
        }

        dout.writeUTF("");
        dout.write(Amf0Type.OBJECT_END.ordinal());
    }
}

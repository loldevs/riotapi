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

package net.boreeas.riotapi.rtmp.p2.serialization.amf3;

import lombok.Setter;
import lombok.SneakyThrows;
import net.boreeas.riotapi.rtmp.p2.serialization.AmfReader;
import net.boreeas.riotapi.rtmp.p2.serialization.SerializationContext;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created on 5/18/2014.
 */
public class Amf3ObjectDeserializer {

    @Setter protected AmfReader reader;

    @SneakyThrows({IllegalArgumentException.class, IllegalAccessException.class, NoSuchFieldException.class})
    public void deserialize(Object obj, SerializationContext ctx, DataInputStream in) throws IOException {
        if (ctx.members().length == 0) {
            Set<String> excludes = new HashSet<>(Arrays.asList(ctx.excludes()));

            for (Field f: obj.getClass().getDeclaredFields()) {
                if (excludes.contains(f.getName()) || Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                    continue;
                }

                f.setAccessible(true);
                f.set(obj, reader.decodeAmf3());
            }
        } else {
            for (String name: ctx.members()) {

                Field f = obj.getClass().getDeclaredField(name);
                f.setAccessible(true);
                f.set(obj, reader.decodeAmf3());
            }
        }

        if (ctx.dynamic()) {
            String name;
            while (!(name = in.readUTF()).isEmpty()) {

                if (obj instanceof DynamicObject) {
                    ((DynamicObject) obj).getDynamicMembers().put(name, reader.decodeAmf3());
                } else {
                    // see if we can serialize into fields
                    Field f = obj.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    f.set(obj, reader.decodeAmf3());
                }
            }
        }
    }
}

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

package net.boreeas.riotapi.rtmp.serialization.amf0;

import lombok.Setter;
import lombok.SneakyThrows;
import net.boreeas.riotapi.rtmp.serialization.*;

import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created on 5/8/2014.
 */
public class Amf0ObjectSerializer implements AmfSerializer {

    @Setter protected AmfWriter writer;

    @Override
    public void serialize(Object obj, DataOutputStream out) throws IOException {

        Serialization context = obj.getClass().getAnnotation(Serialization.class);

        if (!context.name().isEmpty()) {
            out.writeUTF(context.name());
        }

        if (obj instanceof Externalizable) {
            ((Externalizable) obj).writeExternal(writer);
        } else {
            serializeAnonymous(obj, out);
        }
    }

    @SneakyThrows(value = {IllegalAccessException.class})
    public void serializeAnonymous(Object obj, DataOutputStream out) throws IOException {

        Class c = obj.getClass();

        while (c != null) {
            for (Field f : c.getDeclaredFields()) {
                if (Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers()) || f.isAnnotationPresent(NoSerialization.class)) {
                    continue;
                }

                f.setAccessible(true);
                String name = f.getName();
                if (f.isAnnotationPresent(SerializedName.class)) {
                    name = f.getAnnotation(SerializedName.class).name();
                }

                out.writeUTF(name);
                writer.encodeAmf0(f.get(obj));
            }

            c = c.getSuperclass();
        }

        out.writeUTF("");
        out.write(Amf0Type.OBJECT_END.ordinal());
    }
}

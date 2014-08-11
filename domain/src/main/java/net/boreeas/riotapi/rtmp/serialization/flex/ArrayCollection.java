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

package net.boreeas.riotapi.rtmp.serialization.flex;

import lombok.Delegate;
import lombok.SneakyThrows;
import net.boreeas.riotapi.rtmp.serialization.AmfType;
import net.boreeas.riotapi.rtmp.serialization.Serialization;
import net.boreeas.riotapi.rtmp.serialization.TypeConverter;
import net.boreeas.riotapi.rtmp.serialization.amf3.Amf3Type;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/26/2014.
 */
@AmfType(amf3Type = Amf3Type.OBJECT)
@Serialization(name = "flex.messaging.io.ArrayCollection", externalizable = true)
public class ArrayCollection<E> extends ArrayList<E> implements Externalizable {
    @Delegate
    private ArrayList<E> source = new ArrayList<>();

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(source.toArray());
    }

    @Override
    @SneakyThrows(value = {InstantiationException.class, IllegalAccessException.class})
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        source.addAll((List) TypeConverter.typecast(List.class, in.readObject(), false));
    }
}

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

import net.boreeas.riotapi.rtmp.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.serialization.AmfSerializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created on 5/11/2014.
 */
public class Amf0MapSerializer implements AmfSerializer<Map<?, ?>> {

    private AmfWriter writer;

    public Amf0MapSerializer(AmfWriter writer) {
        this.writer = writer;
    }

    @Override
    public void serialize(Map<?, ?> map, DataOutputStream out) throws IOException {
        int len = map.size();
        out.write(len >> 24);
        out.write(len >> 16);
        out.write(len >> 8);
        out.write(len);

        for (Map.Entry entry: map.entrySet()) {
            writer.serializeAmf0(entry.getKey().toString());
            writer.encodeAmf0(entry.getValue());
        }

        out.write(0);
        out.write(0);
        out.write(Amf0Type.OBJECT_END.ordinal());
    }
}

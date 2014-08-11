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

import net.boreeas.riotapi.rtmp.serialization.AmfSerializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created on 5/10/2014.
 */
public enum Amf0DateSerializer implements AmfSerializer<Date> {
    INSTANCE;

    @Override
    public void serialize(Date date, DataOutputStream out) throws IOException {
        long time = date.getTime();
        new DataOutputStream(out).writeDouble(time);

        out.write(0); // (unused) time zone info
        out.write(0);
    }
}

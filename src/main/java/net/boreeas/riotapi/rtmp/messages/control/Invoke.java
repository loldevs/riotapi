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

package net.boreeas.riotapi.rtmp.p2.messages.control;

import net.boreeas.riotapi.rtmp.p2.MessageType;
import net.boreeas.riotapi.rtmp.p2.messages.Status;
import net.boreeas.riotapi.rtmp.p2.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.p2.serialization.ObjectEncoding;

import java.io.IOException;

/**
 * Created on 5/19/2014.
 */
public class Invoke extends Command {
    public Invoke(MessageType type) {
        super(type);
    }

    @Override
    public void writeBody(AmfWriter writer) throws IOException {
        ObjectEncoding encoding = getType() == MessageType.INVOKE ? ObjectEncoding.AMF3 : ObjectEncoding.AMF0;
        boolean isRequest = getMethod().getStatus() == CallStatus.REQUEST;

        if (isRequest) {
            writer.encode(getMethod().getName(), encoding);
        } else {
            writer.encode(getMethod().isSuccess() ? "_result" : "_error", encoding);
        }

        writer.encode(getInvokeId(), encoding);
        writer.encode(getConnectionParams(), encoding);

        if (isRequest) {
            for (Object obj: getMethod().getParams()) {
                writer.encode(obj, encoding);
            }
        } else {
            if (!getMethod().isSuccess()) {
                getMethod().setParams(new Status(Status.CALL_FAILED, "error", "Call failed."));
            }
            writer.encode(getMethod().getParams(), encoding);
        }
    }
}

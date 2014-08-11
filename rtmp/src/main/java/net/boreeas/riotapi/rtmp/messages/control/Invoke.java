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

package net.boreeas.riotapi.rtmp.messages.control;

import net.boreeas.riotapi.rtmp.MessageType;
import net.boreeas.riotapi.rtmp.messages.Status;
import net.boreeas.riotapi.rtmp.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.serialization.ObjectEncoding;

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
        ObjectEncoding encoding = getType() == MessageType.INVOKE_AMF3 ? ObjectEncoding.AMF3 : ObjectEncoding.AMF0;
        boolean isRequest = getMethod().getStatus() == CallStatus.REQUEST;

        if (isRequest) {
            writer.encode(getMethod().getName(), ObjectEncoding.AMF0);
        } else {
            writer.encode(getMethod().isSuccess() ? "_result" : "_error", ObjectEncoding.AMF0);
        }

        writer.encode(getInvokeId(), ObjectEncoding.AMF0);
        writer.encode(getConnectionParams(), ObjectEncoding.AMF0);

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

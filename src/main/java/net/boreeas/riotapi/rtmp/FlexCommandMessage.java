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

package net.boreeas.riotapi.rtmp;

import net.boreeas.riotapi.rtmp.amf.AmfObject;
import net.boreeas.riotapi.rtmp.amf.TraitDefinition;
import net.boreeas.riotapi.rtmp.amf.TypedObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created on 4/20/2014.
 */
public class FlexCommandMessage extends AmfObject {
    public static final int SUBSCRIBE = 0;
    public static final int PING = 5;

    private static final TraitDefinition commandMessageDef = new TraitDefinition("flex.messaging.messages.CommandMessage", false, true);
    static {
        commandMessageDef.addMember("messageRefType");
        commandMessageDef.addMember("operation");
        commandMessageDef.addMember("correlationId");
        commandMessageDef.addMember("clientId");
        commandMessageDef.addMember("destination");
        commandMessageDef.addMember("messageId");
        commandMessageDef.addMember("timestamp");
        commandMessageDef.addMember("timeToLive");
        commandMessageDef.addMember("body");
        commandMessageDef.addMember("headers");
    }

    public FlexCommandMessage(int operation) {
        super(commandMessageDef);

        setField("messageRefType", TypedObject.NULL);
        setField("operation", operation);
        setField("correlationId", "");
        setField("clientId", TypedObject.NULL);
        setField("destination", "");
        setField("messageId", UUID.randomUUID().toString());
        setField("timestamp", 0.0);
        setField("timeToLive", 0.0);
        setField("body", new AmfObject(new TraitDefinition("", false, true)));

        Map<String, TypedObject> headers = new HashMap<>();
        headers.put("DSMessagingVersion", TypedObject.fromObject(1.0));
        headers.put("DSId", TypedObject.fromObject("my-rtmps"));
        setField("headers", headers);
    }
}

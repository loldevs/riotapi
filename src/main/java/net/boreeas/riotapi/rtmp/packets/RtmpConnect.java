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

package net.boreeas.riotapi.rtmp.packets;

import lombok.AllArgsConstructor;
import lombok.ToString;
import net.boreeas.riotapi.rtmp.FlexCommandMessage;
import net.boreeas.riotapi.rtmp.amf.DataType;
import net.boreeas.riotapi.rtmp.amf.TypedObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 4/19/2014.
 */
public class RtmpConnect extends RtmpInvoke {
    public RtmpConnect(ConnectInfo ci, FlexCommandMessage cmdMsg) {
        super("connect", 1, false,
                new TypedObject(DataType.AMF3_FROM_AMF0, makeMap(ci)),
                false,
                "nil",
                "",
                new TypedObject(DataType.AMF3_FROM_AMF0, cmdMsg));
    }

    private static Map<String, TypedObject> makeMap(ConnectInfo ci) {

        Map<String, TypedObject> result = new HashMap<>();
        result.put("app", TypedObject.fromObject(ci.app));
        result.put("flashVer", TypedObject.fromObject(ci.flashVer));
        result.put("swfUrl", TypedObject.fromObject(ci.swfUrl));
        result.put("tcUrl", TypedObject.fromObject(ci.tcUrl));
        result.put("fpad", TypedObject.fromObject(ci.proxy));
        result.put("audioCodecs", TypedObject.fromObject(ci.audioCodecs));
        result.put("videoCodecs", TypedObject.fromObject(ci.videoCodecs));
        result.put("videoFunctions", TypedObject.fromObject(ci.videoFunctions));
        result.put("pageUrl", TypedObject.fromObject(ci.pageUrl));
        result.put("objectEncoding", TypedObject.fromObject(ci.objectEncoding));

        return result;
    }

    @AllArgsConstructor
    @ToString
    public static class ConnectInfo {
        String app;
        String flashVer;
        String swfUrl;
        String tcUrl;
        boolean proxy;
        int audioCodecs;
        int videoCodecs;
        int videoFunctions;
        String pageUrl;
        int objectEncoding;
    }
}

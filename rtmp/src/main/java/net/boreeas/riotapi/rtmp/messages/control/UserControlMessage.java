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

import lombok.Getter;
import lombok.ToString;
import net.boreeas.riotapi.rtmp.MessageType;
import net.boreeas.riotapi.rtmp.RtmpEvent;
import net.boreeas.riotapi.rtmp.serialization.AmfWriter;

import java.io.IOException;
import java.util.List;

/**
 * Created on 5/18/2014.
 */
@ToString
public class UserControlMessage extends RtmpEvent {
    @Getter private Type controlMessageType;
    @Getter private List<Integer> values;

    public UserControlMessage(Type controlMessageType, List<Integer> values) {
        super(MessageType.USER_CONTROL_MESSAGE);
        this.controlMessageType = controlMessageType;
        this.values = values;
    }

    public void writeBody(AmfWriter writer) throws IOException {
        writer.writeShort(controlMessageType.ordinal());
        for (int i: values) {
            writer.writeInt(i);
        }
    }

    public enum Type {
        STREAM_BEGIN,
        STREAM_EOF,
        STREAM_DRY,
        SET_BUFFER_LENGTH,
        STREAM_IS_RECORDED,
        __UNUSED__,
        PING_REQUEST,
        PING_RESPONSE
    }
}

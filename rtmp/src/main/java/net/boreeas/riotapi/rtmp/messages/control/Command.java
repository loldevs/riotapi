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
import lombok.Setter;
import lombok.ToString;
import net.boreeas.riotapi.rtmp.MessageType;
import net.boreeas.riotapi.rtmp.RtmpEvent;

/**
 * Created on 5/18/2014.
 */
@Getter
@Setter
@ToString
public abstract class Command extends RtmpEvent {
    private Method method;
    private byte[] buffer;
    private int invokeId;
    private Object connectionParams;

    public Command(MessageType type) {
        super(type);
    }


    @Getter
    @ToString
    public static class Method {
        private CallStatus status;
        private String name;
        private boolean success;
        private Object[] params;

        public Method(String name, Object[] params) {
            this.name = name;
            this.params = params;
            status = CallStatus.REQUEST;
        }

        public void setParams(Object... params) {
            this.params = params;
        }
    }

    public enum CallStatus {
        REQUEST,
        RESULT
    }
}

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

package net.boreeas.riotapi.rtmp.messages;

import lombok.Getter;
import lombok.Setter;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

/**
 * Created on 6/11/2014.
 */
@Serialization(name="flex.messaging.messages.CommandMessage", noncanonicalNames={"DSC"})
public class CommandMessage extends AsyncMessage {
    public enum Operation {
        SUBSCRIBE(0),
        UNSUBSCRIBE(0),
        POLL(2),
        DATA_UPDATE_ATTRIBUTES(3),
        CLIENT_SYNC(4),
        CLIENT_PING(5),
        DATA_UPDATE(7),
        CLUSTER_REQUEST(7),
        LOGIN(8),
        LOGOUT(9),
        INVALIDATE_SUBSCRIBTION(10),
        CHANNEL_DISCONNECTED(12),
        UNKNOWN(10000);

        public final int id;
        private Operation(int id) { this.id = id; }
    }

    @Getter @Setter private String messageRefType;
    private int operation;

    public CommandMessage(){
    }

    public CommandMessage(String refType, Operation operation) {
        this.messageRefType = refType;
        this.operation = operation.id;
    }



    public void setOperation(Operation op) {
        this.operation = op.id;
    }

    public Operation getOperation() {
        switch(this.operation) {
            case  0: return Operation.SUBSCRIBE;
            case  1: return Operation.UNSUBSCRIBE;
            case  2: return Operation.POLL;
            case  3: return Operation.DATA_UPDATE_ATTRIBUTES;
            case  4: return Operation.CLIENT_SYNC;
            case  5: return Operation.CLIENT_PING;
            case  7: return Operation.CLUSTER_REQUEST;
            case  8: return Operation.LOGIN;
            case  9: return Operation.LOGOUT;
            case 10: return Operation.INVALIDATE_SUBSCRIBTION;
            case 12: return Operation.CHANNEL_DISCONNECTED;
            default: return Operation.UNKNOWN;
        }
    }
}

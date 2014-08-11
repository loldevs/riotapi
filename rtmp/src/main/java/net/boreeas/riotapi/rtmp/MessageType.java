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

package net.boreeas.riotapi.rtmp;

/**
 * Created on 4/25/2014.
 */
public enum MessageType {

    SET_CHUNK_SIZE(1),
    ABORT_MESSAGE(2),
    ACKNOWLEDGEMENT(3),
    USER_CONTROL_MESSAGE(4),
    WINDOW_ACKNOWLEDGEMENT_SIZE(5),
    SET_PEER_BANDWIDTH(6),

    AUDIO(8),
    VIDEO(9),

    DATA_AMF3(15),
    SHARED_OBJ_AMF3(16),
    INVOKE_AMF3(17),

    DATA_AMF0(18),
    SHARED_OBJ_AMF0(19),
    INVOKE_AMF0(20),

    AGGREGATE(22);


    public final int id;

    private MessageType(int id) {
        this.id = id;
    }

    public static final MessageType getById(int id) {
        for (MessageType type: values()) {
            if (type.id == id) {
                return type;
            }
        }

        return null;
    }
}

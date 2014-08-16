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

package net.boreeas.riotapi.com.riotgames.platform.messaging;

import lombok.Getter;
import lombok.Setter;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.List;

/**
 * Created on 8/2/2014.
 */
@Getter
@Setter
@Serialization(name = "com.riotgames.platform.messaging.PlatformException")
public class PlatformException extends RuntimeException {
    private String errorCode;
    private List<Object> substitutionArguments;
    private String rootCauseClassname;
    private Object cause;
    private String message;
    private String localizedMessage;

    public PlatformException() {}

    public PlatformException(String s) {
        super(s);
    }

    public PlatformException(String s, Throwable cause) {
        super(s, cause);
    }

    public Throwable getCause() {
        return (cause instanceof Throwable) ? (Throwable) cause : super.getCause();
    }

}

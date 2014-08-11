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

/**
 * Created on 6/3/2014.
 */
public class InvokeException extends RuntimeException {
    @Getter private ErrorMessage err;

    public InvokeException() {

    }

    public InvokeException(ErrorMessage err) {
        super(formatErrorMessage(err),
                err.getRootCause() instanceof Throwable ? (Throwable) err.getRootCause() : null);
        this.err = err;
    }

    private static String formatErrorMessage(ErrorMessage err) {
        String base = err.getFaultCode() + ": " + err.getFaultString();
        if (err.getFaultDetail() != null) {
            base += "\nDetail:     " + err.getFaultDetail();
        }
        if (err.getRootCause() != null && !(err.getRootCause() instanceof  Throwable)) {
            base += "\nRoot cause: " + err.getRootCause();
        }
        if (err.getExtendedData() != null) {
            base += "\nExtended:   " + err.getExtendedData();
        }

        return base;
    }
}

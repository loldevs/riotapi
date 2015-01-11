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

package net.boreeas.riotapi;

/**
 * Created on 4/14/2014.
 */
public class RequestException extends RuntimeException {
    private ErrorType error;
    private int code;

    public RequestException(String msg) {
        super(msg);
    }

    public RequestException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RequestException(int responseCode) {
        this(responseCode, ErrorType.getByCode(responseCode));
    }

    public RequestException(int responseCode, ErrorType error) {
        super(responseCode + "/" + error + " error during request");
        this.error = error;
        this.code = responseCode;
    }

    public ErrorType getErrorType() {
        return error;
    }

    public int getErrorCode() { return code; }


    public enum ErrorType {
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        NOT_FOUND(404),
        RATE_LIMIT_EXCEEDED(429),
        INTERNAL_SERVER_ERROR(500),
        SERVICE_UNAVAILABLE(503),
        CLOUDFLARE_GENERIC(520),
        CLOUDFLARE_CONNECTION_REFUSED(521),
        CLOUDFLARE_TIMEOUT(522),
        CLOUDFLARE_SSL_HANDSHAKE_FAILED(525);

        public final int code;

        private ErrorType(int code) {
            this.code = code;
        }

        public static ErrorType getByCode(int code) {
            for (ErrorType type: values()) {
                if (type.code == code) {
                    return type;
                }
            }
            return null;
        }
    }
}

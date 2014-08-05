/*
 * Copyright 2014 Malte Schütze
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

    public RequestException(int responseCode, ErrorType error) {
        super(responseCode + "/" + error + " error during request");
        this.error = error;
    }

    public ErrorType getErrorType() {
        return error;
    }



    public enum ErrorType {
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        NOT_FOUND(404),
        RATE_LIMIT_EXCEEDED(429),
        INTERNAL_SERVER_ERROR(500),
        SERVICE_UNAVAILABLE(503);

        public final int code;

        private ErrorType(int code) {
            this.code = code;
        }

        public static ErrorType getByCode(int code) {
            switch (code) {
                case 400: return BAD_REQUEST;
                case 401: return UNAUTHORIZED;
                case 404: return NOT_FOUND;
                case 429: return RATE_LIMIT_EXCEEDED;
                case 500: return INTERNAL_SERVER_ERROR;
                case 503: return SERVICE_UNAVAILABLE;
                default: return null;
            }
        }
    }
}

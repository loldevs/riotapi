package net.boreeas.riotapi.rest;

/**
 * Created on 4/14/2014.
 */
public class RequestException extends RuntimeException {
    private ErrorType error;

    public RequestException(ErrorType error) {
        super(error + " error during request");
        this.error = error;
    }

    public ErrorType getErrorType() {
        return error;
    }



    public enum ErrorType {
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        NOT_FOUND(404),
        RATE_LIMIT_EXCEEDED(421),
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
                case 421: return RATE_LIMIT_EXCEEDED;
                case 500: return INTERNAL_SERVER_ERROR;
                case 503: return SERVICE_UNAVAILABLE;
                default: return null;
            }
        }
    }
}

package com.volcanoisland.reservationsapi.exception;

import org.springframework.http.HttpStatus;

public class MethodNotAllowedException extends RuntimeException {


    public MethodNotAllowedException() {
        super(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
    }

    public MethodNotAllowedException(Throwable cause) {
        super(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), cause);
    }

    public MethodNotAllowedException(String message) {
        super(message);
    }

    public MethodNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.volcanoisland.reservationsapi.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    public NotFoundException(Throwable cause) {
        super(HttpStatus.NOT_FOUND.getReasonPhrase(), cause);
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

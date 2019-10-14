package com.volcanoisland.reservationsapi.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {


    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    public BadRequestException(Throwable cause) {
        super(HttpStatus.BAD_REQUEST.getReasonPhrase(), cause);
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

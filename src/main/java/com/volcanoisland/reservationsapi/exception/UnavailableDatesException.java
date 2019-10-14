package com.volcanoisland.reservationsapi.exception;

public class UnavailableDatesException extends RuntimeException {

    private static final String UNAVAILABLE_DATE_MESSAGE = "At least one of the requested days is not available";

    public UnavailableDatesException() {
        super(UNAVAILABLE_DATE_MESSAGE);
    }

    public UnavailableDatesException(Throwable cause) {
        super(UNAVAILABLE_DATE_MESSAGE, cause);
    }

    public UnavailableDatesException(String message) {
        super(message);
    }

    public UnavailableDatesException(String message, Throwable cause) {
        super(message, cause);
    }
}

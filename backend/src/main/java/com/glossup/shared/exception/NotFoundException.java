package com.glossup.shared.exception;

/** Se lanza cuando un recurso solicitado no existe. Se traduce a HTTP 404. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

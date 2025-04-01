package com.event_manager.photo_hub.exceptions;

public class InvalidJwtTokenException extends RuntimeException {

    public InvalidJwtTokenException(String message) {
        super(message);
    }
}

package com.event_manager.photo_hub.exceptions;

public class InvalidJwtTokenException extends Exception {

    public InvalidJwtTokenException(String message) {
        super(message);
    }
}

package com.event_manager.photo_hub.exceptions;

public class InvalidRegistrationCodeException extends RuntimeException {

    public InvalidRegistrationCodeException(String message) {
        super(message);
    }
}

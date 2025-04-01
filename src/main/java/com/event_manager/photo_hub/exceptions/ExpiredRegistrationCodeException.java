package com.event_manager.photo_hub.exceptions;

public class ExpiredRegistrationCodeException extends RuntimeException {

    public ExpiredRegistrationCodeException(String message) {
        super(message);
    }
}

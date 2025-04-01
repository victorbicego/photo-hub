package com.event_manager.photo_hub.controllers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.event_manager.photo_hub.models.ApiResponse;

public class ControllerUtilService {

    private ControllerUtilService() {
    }

    public static <T> ResponseEntity<ApiResponse<T>> buildResponse(
            HttpStatus status, T body, String message) {
        ApiResponse<T> response = new ApiResponse<>(status, body, message);
        return ResponseEntity.status(status).body(response);
    }

    public static void validateDateRange(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "The 'from' date must be earlier than or equal to the 'to' date.");
        }
    }
}

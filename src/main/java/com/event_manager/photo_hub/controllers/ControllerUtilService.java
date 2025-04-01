package com.event_manager.photo_hub.controllers;

import com.event_manager.photo_hub.models.ApiResponse;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControllerUtilService {

  private ControllerUtilService() {}

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

  public static void validateFromDateInFuture(LocalDateTime from) {
    if (from.isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("The 'from' date must be in the future.");
    }
  }

  public static void validateMax7Days(LocalDateTime from, LocalDateTime to) {
    if (from.plusDays(7).isBefore(to)) {
      throw new IllegalArgumentException("The date range must not exceed 7 days.");
    }
  }
}

package com.event_manager.photo_hub.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.event_manager.photo_hub.models.ApiResponse;

public class ControllerUtilService {

  private static final Long MAX_DAYS = 7L;

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
    if (from.isBefore(ZonedDateTime.now(ZoneId.systemDefault())
            .toLocalDateTime())) {
      throw new IllegalArgumentException("The 'from' date must be in the future.");
    }
  }

  public static void validateMax7Days(LocalDateTime from, LocalDateTime to) {
    if (from.plusDays(MAX_DAYS).isBefore(to)) {
      throw new IllegalArgumentException(
          String.format("The date range must not exceed %d days.", MAX_DAYS));
    }
  }
}

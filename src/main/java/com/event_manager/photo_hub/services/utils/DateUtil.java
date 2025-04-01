package com.event_manager.photo_hub.services.utils;

import java.time.LocalDateTime;

public class DateUtil {

  private DateUtil() {}

  public static void validateDateRange(LocalDateTime from, LocalDateTime to) {
    if (from.isAfter(to)) {
      throw new IllegalArgumentException(
          "The 'from' date must be earlier than or equal to the 'to' date.");
    }
  }
}

package com.event_manager.photo_hub.services.utils;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.models.Role;
import org.springframework.security.core.userdetails.UserDetails;

public class RoleUtil {

  private RoleUtil() {}

  public static Role determineRole(UserDetails userDetails) {
    String roleString =
        userDetails.getAuthorities().stream()
            .findFirst()
            .orElseThrow(() -> new BadRequestException("Invalid user role."))
            .getAuthority();
    return Role.valueOf(roleString);
  }
}

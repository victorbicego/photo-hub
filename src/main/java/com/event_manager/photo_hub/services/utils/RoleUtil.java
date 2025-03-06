package com.event_manager.photo_hub.services.utils;

import org.springframework.security.core.userdetails.UserDetails;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.models.Role;

public class RoleUtil {

  public static Role determineRole(UserDetails userDetails) throws BadRequestException {
    String roleString =
        userDetails.getAuthorities().stream()
            .findFirst()
            .orElseThrow(() -> new BadRequestException("Invalid user role."))
            .getAuthority();
    return Role.valueOf(roleString);
  }
}

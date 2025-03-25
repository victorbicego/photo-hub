package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

  UserDetails activateUser(String username) throws BadRequestException;

  void updatePassword(String username, String newPassword) throws BadRequestException;
}

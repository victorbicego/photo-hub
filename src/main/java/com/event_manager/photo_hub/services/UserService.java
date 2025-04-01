package com.event_manager.photo_hub.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

  UserDetails activateUser(String username);

  void updatePassword(String username, String newPassword);
}

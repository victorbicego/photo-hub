package com.event_manager.photo_hub.services;

import org.springframework.security.core.userdetails.UserDetails;

import com.event_manager.photo_hub.exceptions.BadRequestException;

public interface UserService {

  UserDetails activateUser(String username) throws BadRequestException;

  void updatePassword(String username, String newPassword) throws BadRequestException;
}

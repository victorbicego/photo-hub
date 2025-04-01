package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.models.Role;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.services.UserService;
import com.event_manager.photo_hub.services.utils.RoleUtil;
import com.event_manager.photo_hub.services_crud.HostCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final CustomUserDetailsService customUserDetailsService;
  private final HostCrudService hostCrudService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails activateUser(String username) {
    UserDetails user = customUserDetailsService.loadUserByUsername(username);
    if (RoleUtil.determineRole(user) == Role.HOST) {
      Host host = (Host) user;
      host.setEnabled(true);
      return hostCrudService.save(host);
    }
    throw new BadRequestException("Invalid user role.");
  }

  @Override
  public void updatePassword(String username, String newPassword) {
    UserDetails user = customUserDetailsService.loadUserByUsername(username);
    if (RoleUtil.determineRole(user) == Role.HOST) {
      Host host = (Host) user;
      host.setPassword(passwordEncoder.encode(newPassword));
      hostCrudService.save(host);
      return;
    }
    throw new BadRequestException("Invalid user role.");
  }
}

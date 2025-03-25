package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.Role;
import com.event_manager.photo_hub.models.dtos.LoginRequestDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import com.event_manager.photo_hub.services.AuthenticationService;
import com.event_manager.photo_hub.services.JwtService;
import com.event_manager.photo_hub.services.utils.RoleUtil;
import com.event_manager.photo_hub.services_crud.EventCrudService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final CustomUserDetailsService customUserDetailsService;
  private final EventCrudService eventCrudService;

  @Override
  public LoginResponseDto authenticate(LoginRequestDto loginRequestDto) throws BadRequestException {
    authenticateCredentials(loginRequestDto);
    UserDetails foundUser =
        customUserDetailsService.loadUserByUsername(loginRequestDto.getUsername());
    Cookie cookie = jwtService.createCookie(foundUser);
    Role role = RoleUtil.determineRole(foundUser);
    return new LoginResponseDto(cookie, foundUser.getUsername(), role);
  }

  @Override
  public Cookie logout() {
    return jwtService.createLogoutCookie();
  }

  @Override
  public Cookie authenticateQrCode(String qrCode) throws NotFoundException {
    eventCrudService.findByQrCode(qrCode);
    return jwtService.createQrCodeCookie(qrCode);
  }

  @Override
  public Cookie eventLogout() {
    return jwtService.createEventLogoutCookie();
  }

  private void authenticateCredentials(LoginRequestDto loginRequestDto) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequestDto.getUsername(), loginRequestDto.getPassword()));
  }
}

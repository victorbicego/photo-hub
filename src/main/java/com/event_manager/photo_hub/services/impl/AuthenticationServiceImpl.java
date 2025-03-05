package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.models.Role;
import com.event_manager.photo_hub.models.dtos.LoginRequestDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import com.event_manager.photo_hub.services.AuthenticationService;
import com.event_manager.photo_hub.services.JwtService;
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

  @Override
  public LoginResponseDto authenticate(LoginRequestDto loginRequestDto) throws BadRequestException {
    authenticateCredentials(loginRequestDto);
    UserDetails foundUser = customUserDetailsService.loadUserByUsername(loginRequestDto.getUsername());
    String jwtToken = jwtService.generateToken(foundUser);
    Cookie cookie = jwtService.createCookie(jwtToken);
    Role role = determineRole(foundUser);
    return new LoginResponseDto(cookie, foundUser.getUsername(), role);
  }

  private void authenticateCredentials(LoginRequestDto loginRequestDto) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequestDto.getUsername(), loginRequestDto.getPassword()));
  }

  private Role determineRole(UserDetails userDetails) throws BadRequestException {
    String roleString =
        userDetails.getAuthorities().stream()
            .findFirst()
            .orElseThrow(() -> new BadRequestException("Invalid user role."))
            .getAuthority();
    return Role.valueOf(roleString);
  }
}

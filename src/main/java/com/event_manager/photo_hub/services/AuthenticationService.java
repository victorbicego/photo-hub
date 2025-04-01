package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.models.dtos.LoginRequestDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import jakarta.servlet.http.Cookie;

public interface AuthenticationService {

  LoginResponseDto authenticate(LoginRequestDto loginRequestDto);

  Cookie authenticateQrCode(String qrCode);

  Cookie logout();

  Cookie logoutQrCode();
}

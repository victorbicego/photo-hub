package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.LoginRequestDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import jakarta.servlet.http.Cookie;

public interface AuthenticationService {

  LoginResponseDto authenticate(LoginRequestDto loginRequestDto) throws BadRequestException;

  Cookie logout();

  Cookie authenticateQrCode(String qrCode) throws NotFoundException;

  Cookie eventLogout();
}

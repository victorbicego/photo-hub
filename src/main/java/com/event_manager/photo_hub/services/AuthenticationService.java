package com.event_manager.photo_hub.services;


import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.models.dtos.LoginRequestDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;

public interface AuthenticationService {

    LoginResponseDto authenticate(LoginRequestDto loginRequestDto) throws BadRequestException;
}

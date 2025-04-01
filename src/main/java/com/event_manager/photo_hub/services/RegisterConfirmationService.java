package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import com.event_manager.photo_hub.models.dtos.RegisterConfirmationDto;
import jakarta.mail.MessagingException;

public interface RegisterConfirmationService {

  LoginResponseDto confirmRegistration(RegisterConfirmationDto registerConfirmationDto);

  void resendConfirmationCode(EmailDto emailDto) throws MessagingException;
}

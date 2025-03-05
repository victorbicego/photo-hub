package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.ExpiredRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.InvalidRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import com.event_manager.photo_hub.models.dtos.RegisterConfirmationDto;
import jakarta.mail.MessagingException;

public interface RegisterConfirmationService {

  LoginResponseDto confirmRegistration(RegisterConfirmationDto registerConfirmationDto)
      throws NotFoundException, ExpiredRegistrationCodeException, InvalidRegistrationCodeException, BadRequestException;

  void resendConfirmationCode(EmailDto emailDto) throws NotFoundException, MessagingException;
}

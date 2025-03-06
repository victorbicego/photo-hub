package com.event_manager.photo_hub.services;

import jakarta.mail.MessagingException;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.ExpiredRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.ResetPasswordRequestDto;

public interface ResetPasswordRequestService {

  void sendResetPasswordEmail(EmailDto emailDto) throws MessagingException;

  void confirmNewPassword(ResetPasswordRequestDto resetPasswordRequestDto)
      throws NotFoundException, ExpiredRegistrationCodeException, BadRequestException;
}

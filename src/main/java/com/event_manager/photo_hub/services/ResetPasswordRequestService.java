package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.ResetPasswordRequestDto;
import jakarta.mail.MessagingException;

public interface ResetPasswordRequestService {

  void sendResetPasswordEmail(EmailDto emailDto) throws MessagingException;

  void confirmNewPassword(ResetPasswordRequestDto resetPasswordRequestDto);
}

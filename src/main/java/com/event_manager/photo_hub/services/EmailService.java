package com.event_manager.photo_hub.services;

import jakarta.mail.MessagingException;

public interface EmailService {

  void sendResetPasswordRequestEmail(String username, String code) throws MessagingException;

  void sendConfirmationCode(String username, String code) throws MessagingException;

  void resendConfirmationCode(String username, String code) throws MessagingException;
}

package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.exceptions.ExpiredRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.InvalidRegistrationCodeException;
import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import com.event_manager.photo_hub.models.dtos.RegisterConfirmationDto;
import com.event_manager.photo_hub.models.entities.RegisterConfirmation;
import com.event_manager.photo_hub.services.EmailService;
import com.event_manager.photo_hub.services.JwtService;
import com.event_manager.photo_hub.services.RegisterConfirmationService;
import com.event_manager.photo_hub.services.UserService;
import com.event_manager.photo_hub.services.utils.RoleUtil;
import com.event_manager.photo_hub.services_crud.RegisterConfirmationCrudService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterConfirmationServiceImpl implements RegisterConfirmationService {

  private static final int CODE_EXPIRY_MINUTES = 60;

  private final RegisterConfirmationCrudService registerConfirmationCrudService;
  private final JwtService jwtService;
  private final EmailService emailService;
  private final UserService userService;

  @Override
  public LoginResponseDto confirmRegistration(RegisterConfirmationDto registerConfirmationDto) {
    RegisterConfirmation registerConfirmation =
        registerConfirmationCrudService.findByUsername(registerConfirmationDto.getUsername());
    validateConfirmationCode(registerConfirmation, registerConfirmationDto);
    UserDetails activatedUser = userService.activateUser(registerConfirmation.getUsername());
    registerConfirmationCrudService.delete(registerConfirmation.getId());
    Cookie cookie = jwtService.createCookie(activatedUser);
    return new LoginResponseDto(
        cookie, activatedUser.getUsername(), RoleUtil.determineRole(activatedUser));
  }

  private void validateConfirmationCode(
      RegisterConfirmation registerConfirmation, RegisterConfirmationDto registerConfirmationDto) {
    if (!registerConfirmation.getCode().equals(registerConfirmationDto.getCode())) {
      throw new InvalidRegistrationCodeException("Invalid confirmation code.");
    }
    if (LocalDateTime.now().isAfter(registerConfirmation.getExpiryDate())) {
      throw new ExpiredRegistrationCodeException("Confirmation code has expired.");
    }
  }

  @Override
  public void resendConfirmationCode(EmailDto emailDto) throws MessagingException {
    RegisterConfirmation registerConfirmation =
        registerConfirmationCrudService.findByUsername(emailDto.getUsername());
    RegisterConfirmation refreshedConfirmation = refreshExpiryDate(registerConfirmation);
    emailService.resendConfirmationCode(
        refreshedConfirmation.getUsername(), refreshedConfirmation.getCode());
  }

  private RegisterConfirmation refreshExpiryDate(RegisterConfirmation confirmation) {
    confirmation.setExpiryDate(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
    return registerConfirmationCrudService.save(confirmation);
  }
}

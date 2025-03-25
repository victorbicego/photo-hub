package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.ExpiredRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.InvalidRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
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
  public LoginResponseDto confirmRegistration(RegisterConfirmationDto confirmationDto)
      throws NotFoundException,
          ExpiredRegistrationCodeException,
          InvalidRegistrationCodeException,
          BadRequestException {
    RegisterConfirmation found =
        registerConfirmationCrudService.findByUsername(confirmationDto.getUsername());
    validateConfirmationCode(found, confirmationDto);
    UserDetails activatedUser = userService.activateUser(found.getUsername());
    registerConfirmationCrudService.delete(found.getId());
    Cookie cookie = jwtService.createCookie(activatedUser);
    return new LoginResponseDto(
        cookie, activatedUser.getUsername(), RoleUtil.determineRole(activatedUser));
  }

  private void validateConfirmationCode(
      RegisterConfirmation confirmation, RegisterConfirmationDto dto)
      throws InvalidRegistrationCodeException, ExpiredRegistrationCodeException {
    if (!confirmation.getCode().equals(dto.getCode())) {
      throw new InvalidRegistrationCodeException("Invalid confirmation code.");
    }
    if (LocalDateTime.now().isAfter(confirmation.getExpiryDate())) {
      throw new ExpiredRegistrationCodeException("Confirmation code has expired.");
    }
  }

  @Override
  public void resendConfirmationCode(EmailDto emailDto)
      throws NotFoundException, MessagingException {
    RegisterConfirmation found =
        registerConfirmationCrudService.findByUsername(emailDto.getUsername());
    RegisterConfirmation updated = updateExpiryDate(found);
    emailService.resendConfirmationCode(updated.getUsername(), updated.getCode());
  }

  private RegisterConfirmation updateExpiryDate(RegisterConfirmation confirmation) {
    confirmation.setExpiryDate(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
    return registerConfirmationCrudService.save(confirmation);
  }
}

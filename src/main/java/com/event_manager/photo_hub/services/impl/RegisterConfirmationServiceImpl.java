package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.ExpiredRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.InvalidRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.Role;
import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import com.event_manager.photo_hub.models.dtos.RegisterConfirmationDto;
import com.event_manager.photo_hub.models.entities.Guest;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.models.entities.RegisterConfirmation;
import com.event_manager.photo_hub.services.EmailService;
import com.event_manager.photo_hub.services.JwtService;
import com.event_manager.photo_hub.services.RegisterConfirmationService;
import com.event_manager.photo_hub.services_crud.RegisterConfirmationCrudService;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterConfirmationServiceImpl implements RegisterConfirmationService {

  private static final int CODE_EXPIRY_MINUTES = 60;

  private final RegisterConfirmationCrudService registerConfirmationCrudService;
  private final CustomUserDetailsService customUserDetailsService;
  private final ConsumerCrudService consumerCrudService;
  private final JwtService jwtService;
  private final EmailService emailService;

  @Override
  public LoginResponseDto confirmRegistration(RegisterConfirmationDto confirmationDto)
      throws NotFoundException, ExpiredRegistrationCodeException, InvalidRegistrationCodeException, BadRequestException {
    RegisterConfirmation foundRegisterConfirmation =
        registerConfirmationCrudService.findByUsername(confirmationDto.getUsername());
    validateRegisterConfirmation(foundRegisterConfirmation, confirmationDto);
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(foundRegisterConfirmation.getUsername());
    Role role = determineRole(userDetails);

    if(Role.HOST.equals(role)){
      Host savedHost = enableHost(foundConsumer);
      registerConfirmationCrudService.delete(foundRegisterConfirmation.getId());
      return generateLoginResponse(savedHost);
    } else if (Role.GUEST.equals(role)) {
      Guest savedGuest = enableGuest(foundConsumer);
      registerConfirmationCrudService.delete(foundRegisterConfirmation.getId());
      return generateLoginResponse(savedGuest);
    }else{
        throw new BadRequestException("Invalid user role.");
    }
  }

  private void validateRegisterConfirmation(
      RegisterConfirmation confirmation, RegisterConfirmationDto dto)
      throws InvalidRegistrationCodeException, ExpiredRegistrationCodeException {
    if (!confirmation.getCode().equals(dto.getCode())) {
      throw new InvalidRegistrationCodeException("Invalid confirmation code.");
    }

    if (LocalDateTime.now().isAfter(confirmation.getExpiryDate())) {
      throw new ExpiredRegistrationCodeException("Confirmation code has expired.");
    }
  }

  private Host enableHost(Host host) {
    host.setEnabled(true);
    return consumerCrudService.save(host);
  }

  private Guest enableGuest(Guest guest) {
    guest.setEnabled(true);
    return consumerCrudService.save(guest);
  }

  private LoginResponseDto generateLoginResponse(UserDetails userDetails) throws BadRequestException {
    String jwtToken = jwtService.generateToken(userDetails);
    Cookie cookie = jwtService.createCookie(jwtToken);
    return new LoginResponseDto(cookie, userDetails.getUsername(), role);
  }

  private Role determineRole(UserDetails userDetails) throws BadRequestException {
    String roleString =
            userDetails.getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Invalid user role."))
                    .getAuthority();
    return Role.valueOf(roleString);
  }

  @Override
  public void resendConfirmationCode(EmailDto emailDto)
      throws NotFoundException, MessagingException {
    RegisterConfirmation foundRegisterConfirmation =
        registerConfirmationCrudService.findByUsername(emailDto.getUsername());
    RegisterConfirmation updatedRegisterConfirmation = updateExpiryDate(foundRegisterConfirmation);
    emailService.resendConfirmationCode(
        updatedRegisterConfirmation.getUsername(), updatedRegisterConfirmation.getCode());
  }

  private RegisterConfirmation updateExpiryDate(RegisterConfirmation confirmation) {
    confirmation.setExpiryDate(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
    return registerConfirmationCrudService.save(confirmation);
  }
}

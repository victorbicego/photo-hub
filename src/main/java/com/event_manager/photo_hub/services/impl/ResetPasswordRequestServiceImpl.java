package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.ExpiredRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.Role;
import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.ResetPasswordRequestDto;
import com.event_manager.photo_hub.models.entities.Guest;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.models.entities.ResetPasswordRequest;
import com.event_manager.photo_hub.services.EmailService;
import com.event_manager.photo_hub.services.ResetPasswordRequestService;
import com.event_manager.photo_hub.services_crud.GuestCrudService;
import com.event_manager.photo_hub.services_crud.HostCrudService;
import com.event_manager.photo_hub.services_crud.ResetPasswordRequestCrudService;
import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasswordRequestServiceImpl implements ResetPasswordRequestService {

  private static final int CODE_EXPIRY_MINUTES = 60;
  private static final int RESET_CODE_LENGTH = 10;

  private final ResetPasswordRequestCrudService resetPasswordRequestCrudService;
  private final HostCrudService hostCrudService;
  private final GuestCrudService guestCrudService;
  private final CustomUserDetailsService customUserDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  @Override
  public void sendResetPasswordEmail(EmailDto emailDto) throws MessagingException {
    UserDetails foundUser = customUserDetailsService.loadUserByUsername(emailDto.getUsername());
    ResetPasswordRequest resetPasswordRequest = getOrCreateResetPassword(foundUser.getUsername());
    emailService.sendResetPasswordRequestEmail(
        foundUser.getUsername(), resetPasswordRequest.getCode());
  }

  private ResetPasswordRequest getOrCreateResetPassword(String username) {
    Optional<ResetPasswordRequest> optionalResetPassword =
        resetPasswordRequestCrudService.findResetPasswordByUsername(username);
    if (optionalResetPassword.isPresent()) {
      return updateResetPasswordRequestExpiryDate(optionalResetPassword.get());
    } else {
      return createResetPasswordRequest(username);
    }
  }

  private ResetPasswordRequest updateResetPasswordRequestExpiryDate(
      ResetPasswordRequest resetPasswordRequest) {
    resetPasswordRequest.setExpiryDate(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
    return resetPasswordRequestCrudService.save(resetPasswordRequest);
  }

  private ResetPasswordRequest createResetPasswordRequest(String username) {
    String code = generateRandomCode();
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest(
            null, username, code, LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
    return resetPasswordRequestCrudService.save(resetPasswordRequest);
  }

  private String generateRandomCode() {
    SecureRandom random = new SecureRandom();
    StringBuilder codeBuilder = new StringBuilder(RESET_CODE_LENGTH);
    for (int i = 0; i < RESET_CODE_LENGTH; i++) {
      codeBuilder.append(random.nextInt(10));
    }
    return codeBuilder.toString();
  }

  @Override
  public void confirmNewPassword(ResetPasswordRequestDto resetPasswordRequestDto)
      throws NotFoundException, ExpiredRegistrationCodeException, BadRequestException {
    ResetPasswordRequest foundResetPasswordRequest =
        resetPasswordRequestCrudService.findResetPasswordByCodeAndUsername(
            resetPasswordRequestDto.getCode(), resetPasswordRequestDto.getUsername());
    checkIfRequestPasswordRequestIsExpired(foundResetPasswordRequest);
    UserDetails foundUser =
        customUserDetailsService.loadUserByUsername(resetPasswordRequestDto.getUsername());
    updateUserPassword(foundUser, resetPasswordRequestDto.getPassword());
    resetPasswordRequestCrudService.delete(foundResetPasswordRequest.getId());
  }

  private void updateUserPassword(UserDetails userDetails, String newPassword)
      throws BadRequestException {
    Role userRole = determineUserRole(userDetails);
    if (userRole == Role.HOST || userRole == Role.GUEST) {
      resetHostPassword((Host) userDetails, newPassword);
    } else {
      resetGuestPassword((Guest) userDetails, newPassword);
    }
  }

  private Role determineUserRole(UserDetails userDetails) throws BadRequestException {
    String roleString =
        userDetails.getAuthorities().stream()
            .findFirst()
            .orElseThrow(() -> new BadRequestException("Invalid user role."))
            .getAuthority();
    return Role.valueOf(roleString);
  }

  private void resetHostPassword(Host host, String newPassword) {
    host.setPassword(passwordEncoder.encode(newPassword));
    hostCrudService.save(host);
  }

  private void resetGuestPassword(Guest guest, String newPassword) {
    guest.setPassword(passwordEncoder.encode(newPassword));
    guestCrudService.save(guest);
  }

  private void checkIfRequestPasswordRequestIsExpired(ResetPasswordRequest resetPasswordRequest)
      throws ExpiredRegistrationCodeException {
    if (LocalDateTime.now().isAfter(resetPasswordRequest.getExpiryDate())) {
      throw new ExpiredRegistrationCodeException("Reset password code has expired.");
    }
  }
}

package com.event_manager.photo_hub.services.impl;

import static com.event_manager.photo_hub.services.utils.CodeGeneratorUtil.generateRandomCode;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.ExpiredRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.ResetPasswordRequestDto;
import com.event_manager.photo_hub.models.entities.ResetPasswordRequest;
import com.event_manager.photo_hub.services.EmailService;
import com.event_manager.photo_hub.services.ResetPasswordRequestService;
import com.event_manager.photo_hub.services.UserService;
import com.event_manager.photo_hub.services_crud.ResetPasswordRequestCrudService;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasswordRequestServiceImpl implements ResetPasswordRequestService {

  private static final int CODE_EXPIRY_MINUTES = 60;
  private static final int RESET_CODE_LENGTH = 10;

  private final ResetPasswordRequestCrudService resetPasswordRequestCrudService;
  private final CustomUserDetailsService customUserDetailsService;
  private final EmailService emailService;
  private final UserService userService;

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
    String code = generateRandomCode(RESET_CODE_LENGTH);
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest(
            null, username, code, LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
    return resetPasswordRequestCrudService.save(resetPasswordRequest);
  }

  @Override
  public void confirmNewPassword(ResetPasswordRequestDto resetPasswordRequestDto)
      throws NotFoundException, ExpiredRegistrationCodeException, BadRequestException {
    ResetPasswordRequest foundResetPasswordRequest =
        resetPasswordRequestCrudService.findResetPasswordByCodeAndUsername(
            resetPasswordRequestDto.getCode(), resetPasswordRequestDto.getUsername());
    checkIfRequestPasswordRequestIsExpired(foundResetPasswordRequest);
    userService.updatePassword(
        resetPasswordRequestDto.getUsername(), resetPasswordRequestDto.getPassword());
    resetPasswordRequestCrudService.delete(foundResetPasswordRequest.getId());
  }

  private void checkIfRequestPasswordRequestIsExpired(ResetPasswordRequest resetPasswordRequest)
      throws ExpiredRegistrationCodeException {
    if (LocalDateTime.now().isAfter(resetPasswordRequest.getExpiryDate())) {
      throw new ExpiredRegistrationCodeException("Reset password code has expired.");
    }
  }
}

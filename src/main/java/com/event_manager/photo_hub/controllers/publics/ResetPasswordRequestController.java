package com.event_manager.photo_hub.controllers.publics;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.ExpiredRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.ResetPasswordRequestDto;
import com.event_manager.photo_hub.services.ResetPasswordRequestService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;

@RestController
@RequestMapping("/api/v1/password-reset")
@RequiredArgsConstructor
@Validated
public class ResetPasswordRequestController {

  private final ResetPasswordRequestService resetPasswordRequestService;

  @PostMapping("/request")
  public ResponseEntity<ApiResponse<Void>> sendResetPasswordEmail(
      @Valid @RequestBody EmailDto emailDto) throws MessagingException {

    resetPasswordRequestService.sendResetPasswordEmail(emailDto);

    return buildResponse(HttpStatus.OK, null, "Reset password code sent successfully.");
  }

  @PostMapping("/confirm")
  public ResponseEntity<ApiResponse<Void>> confirmNewPassword(
      @Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto)
      throws NotFoundException, ExpiredRegistrationCodeException, BadRequestException {

    resetPasswordRequestService.confirmNewPassword(resetPasswordRequestDto);

    return buildResponse(HttpStatus.OK, null, "Password reset successfully.");
  }
}

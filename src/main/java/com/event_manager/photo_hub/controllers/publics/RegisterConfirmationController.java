package com.event_manager.photo_hub.controllers.publics;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import com.event_manager.photo_hub.models.dtos.RegisterConfirmationDto;
import com.event_manager.photo_hub.services.RegisterConfirmationService;

@RestController
@RequestMapping("/api/v1/confirmation")
@RequiredArgsConstructor
@Validated
public class RegisterConfirmationController {

    private final RegisterConfirmationService registerConfirmationService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoginResponseDto>> confirmRegistration(
            @Valid @RequestBody RegisterConfirmationDto registerConfirmationDto) {
        LoginResponseDto loginResponseDto =
                registerConfirmationService.confirmRegistration(registerConfirmationDto);
        return buildResponse(HttpStatus.OK, loginResponseDto, "Registration confirmed successfully.");
    }

    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<Void>> resendUserConfirmationCode(
            @Valid @RequestBody EmailDto emailDto) {
        registerConfirmationService.resendConfirmationCode(emailDto);
        return buildResponse(HttpStatus.OK, null, "New confirmation code sent successfully.");
    }
}

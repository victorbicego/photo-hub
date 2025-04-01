package com.event_manager.photo_hub.controllers.publics;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.LoginRequestDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import com.event_manager.photo_hub.services.AuthenticationService;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> authenticate(
            @Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authenticationService.authenticate(loginRequestDto);
        Cookie eventLogoutCookie = authenticationService.eventLogout();
        response.addCookie(eventLogoutCookie);
        response.addCookie(loginResponseDto.getCookie());
        return buildResponse(HttpStatus.OK, loginResponseDto, "Login successful.");
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        Cookie logoutCookie = authenticationService.logout();
        Cookie eventLogoutCookie = authenticationService.eventLogout();
        response.addCookie(logoutCookie);
        response.addCookie(eventLogoutCookie);
        return buildResponse(HttpStatus.OK, null, "Logout successful.");
    }

    @PostMapping("/event/{qrCode}")
    public ResponseEntity<ApiResponse<Void>> authenticateQrCode(
            @PathVariable String qrCode, HttpServletResponse response) {
        Cookie logoutCookie = authenticationService.logout();
        Cookie qrCodeCookie = authenticationService.authenticateQrCode(qrCode);
        response.addCookie(logoutCookie);
        response.addCookie(qrCodeCookie);
        return buildResponse(HttpStatus.OK, null, "Login successful.");
    }
}

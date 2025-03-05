package com.event_manager.photo_hub.controllers.publics;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.LoginRequestDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import com.event_manager.photo_hub.services.AuthenticationService;
import com.event_manager.photo_hub.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponseDto>> authenticate(
          @Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response)
      throws BadRequestException {

    LoginResponseDto loginResponseDto = authenticationService.authenticate(loginRequestDto);
    response.addCookie(loginResponseDto.getCookie());

    return buildResponse(HttpStatus.OK, loginResponseDto, "Login successful.");
  }
}

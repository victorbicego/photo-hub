package com.event_manager.photo_hub.controllers.publics;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.CreateGuestDto;
import com.event_manager.photo_hub.models.dtos.CreateHostDto;
import com.event_manager.photo_hub.models.dtos.GuestDto;
import com.event_manager.photo_hub.models.dtos.HostDto;
import com.event_manager.photo_hub.services.RegisterService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
@Validated
public class RegisterController {

  private final RegisterService registerService;

  @PostMapping("/host")
  public ResponseEntity<ApiResponse<HostDto>> registerHost(
      @Valid @RequestBody CreateHostDto createHostDto) throws MessagingException {
    HostDto hostDto = registerService.registerHost(createHostDto);
    return buildResponse(HttpStatus.CREATED, hostDto, "Host created successfully.");
  }

  @PostMapping("/guest")
  public ResponseEntity<ApiResponse<GuestDto>> registerGuest(
      @Valid @RequestBody CreateGuestDto createGuestDto)
      throws MessagingException, NotFoundException {
    GuestDto guestDto = registerService.registerGuest(createGuestDto);
    return buildResponse(HttpStatus.CREATED, guestDto, "Guest created successfully.");
  }
}

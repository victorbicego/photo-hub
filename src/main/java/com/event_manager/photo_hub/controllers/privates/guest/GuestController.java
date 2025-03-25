package com.event_manager.photo_hub.controllers.privates.guest;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.GuestDto;
import com.event_manager.photo_hub.models.dtos.PasswordDto;
import com.event_manager.photo_hub.models.dtos.UpdateGuestDto;
import com.event_manager.photo_hub.services.GuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/guest")
@RequiredArgsConstructor
@Validated
public class GuestController {

  private final GuestService guestService;

  @GetMapping
  public ResponseEntity<ApiResponse<GuestDto>> getGuestInfo()
      throws InvalidJwtTokenException, NotFoundException {
    GuestDto guestDto = guestService.getInfo();
    return buildResponse(HttpStatus.OK, guestDto, "Guest information retrieved successfully.");
  }

  @PutMapping
  public ResponseEntity<ApiResponse<GuestDto>> updateGuestInfo(
      @RequestBody @Valid UpdateGuestDto updateGuestDto)
      throws InvalidJwtTokenException, NotFoundException {
    GuestDto guestDto = guestService.update(updateGuestDto);
    return buildResponse(HttpStatus.OK, guestDto, "Guest information updated successfully.");
  }

  @PutMapping("/password")
  public ResponseEntity<ApiResponse<GuestDto>> updateGuestPassword(
      @RequestBody @Valid PasswordDto passwordDto)
      throws InvalidJwtTokenException, NotFoundException {
    GuestDto guestDto = guestService.updatePassword(passwordDto);
    return buildResponse(HttpStatus.OK, guestDto, "Guest password updated successfully.");
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteGuestAccount()
      throws InvalidJwtTokenException, NotFoundException {
    guestService.delete();
    return buildResponse(HttpStatus.NO_CONTENT, null, "Guest deleted successfully.");
  }
}

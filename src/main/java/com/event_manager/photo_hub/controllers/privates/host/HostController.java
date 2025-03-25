package com.event_manager.photo_hub.controllers.privates.host;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.HostDto;
import com.event_manager.photo_hub.models.dtos.PasswordDto;
import com.event_manager.photo_hub.models.dtos.UpdateHostDto;
import com.event_manager.photo_hub.services.HostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/host")
@RequiredArgsConstructor
@Validated
public class HostController {

  private final HostService hostService;

  @GetMapping
  public ResponseEntity<ApiResponse<HostDto>> getHostInfo()
      throws InvalidJwtTokenException, NotFoundException {
    HostDto hostDto = hostService.getInfo();
    return buildResponse(HttpStatus.OK, hostDto, "Host information retrieved successfully.");
  }

  @PutMapping
  public ResponseEntity<ApiResponse<HostDto>> updateHostInfo(
      @RequestBody @Valid UpdateHostDto updateHostDto)
      throws InvalidJwtTokenException, NotFoundException {
    HostDto hostDto = hostService.update(updateHostDto);
    return buildResponse(HttpStatus.OK, hostDto, "Host information updated successfully.");
  }

  @PutMapping("/password")
  public ResponseEntity<ApiResponse<HostDto>> updateHostPassword(
      @RequestBody @Valid PasswordDto passwordDto)
      throws InvalidJwtTokenException, NotFoundException {
    HostDto hostDto = hostService.updatePassword(passwordDto);
    return buildResponse(HttpStatus.OK, hostDto, "Host password updated successfully.");
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteHostAccount()
      throws InvalidJwtTokenException, NotFoundException {
    hostService.delete();
    return buildResponse(HttpStatus.NO_CONTENT, null, "Host deleted successfully.");
  }
}

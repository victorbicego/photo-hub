package com.event_manager.photo_hub.controllers.privates;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/host")
@RequiredArgsConstructor
@Validated
public class HostController {

  private final HostService hostService;

  @GetMapping
  public ResponseEntity<ApiResponse<HostDto>> getHost() {
    HostDto hostDto = hostService.get();
    return buildResponse(HttpStatus.OK, hostDto, "Host information retrieved successfully.");
  }

  @PutMapping
  public ResponseEntity<ApiResponse<HostDto>> updateHost(
      @RequestBody @Valid UpdateHostDto updateHostDto) {
    HostDto hostDto = hostService.update(updateHostDto);
    return buildResponse(HttpStatus.OK, hostDto, "Host information updated successfully.");
  }

  @PutMapping("/password")
  public ResponseEntity<ApiResponse<HostDto>> updateHostPassword(
      @RequestBody @Valid PasswordDto passwordDto) {
    HostDto hostDto = hostService.updatePassword(passwordDto);
    return buildResponse(HttpStatus.OK, hostDto, "Host password updated successfully.");
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteHost() {
    hostService.delete();
    return buildResponse(HttpStatus.OK, null, "Host deleted successfully.");
  }
}

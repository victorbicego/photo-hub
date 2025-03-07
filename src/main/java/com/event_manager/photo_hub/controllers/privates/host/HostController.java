package com.event_manager.photo_hub.controllers.privates.host;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;
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

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.HostDto;
import com.event_manager.photo_hub.models.dtos.PasswordDto;
import com.event_manager.photo_hub.models.dtos.UpdateHostDto;
import com.event_manager.photo_hub.services.HostService;

@RestController
@RequestMapping("/api/v1/host")
@RequiredArgsConstructor
@Validated
public class HostController {

    private final HostService hostService;

    @GetMapping
    public ResponseEntity<ApiResponse<HostDto>> getInfo()
            throws InvalidJwtTokenException, NotFoundException {

        HostDto hostDto = hostService.getInfo();

        return buildResponse(
                HttpStatus.OK, hostDto, "Host information retrieved successfully.");
    }

    @PutMapping
    public ResponseEntity<ApiResponse<HostDto>> update(
            @RequestBody @Valid UpdateHostDto updateConsumerDto)
            throws InvalidJwtTokenException, NotFoundException {

        HostDto hostDto = hostService.update(updateConsumerDto);

        return buildResponse(HttpStatus.OK, hostDto, "Host information updated successfully.");
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<HostDto>> updatePassword(
            @RequestBody @Valid PasswordDto updatePasswordDto)
            throws InvalidJwtTokenException, NotFoundException {

        HostDto hostDto = hostService.updatePassword(updatePasswordDto);

        return buildResponse(HttpStatus.OK, hostDto, "Host password updated successfully.");
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete()
            throws InvalidJwtTokenException, NotFoundException {

        hostService.delete();

        return buildResponse(HttpStatus.NO_CONTENT, null, "Host deleted successfully.");
    }
}

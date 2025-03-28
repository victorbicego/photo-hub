package com.event_manager.photo_hub.controllers.privates.guest;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.EventDto;
import com.event_manager.photo_hub.models.dtos.GetSinglePhotoDto;
import com.event_manager.photo_hub.models.dtos.PhotoDto;
import com.event_manager.photo_hub.services.EventService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/guest/event")
@RequiredArgsConstructor
@Validated
public class GuestEventController {

  private final EventService eventService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<EventDto>>> getAllEventsForGuest(
      @RequestParam(defaultValue = "", required = false) String search,
      @RequestParam(defaultValue = "name", required = false) String sortBy,
      @RequestParam(defaultValue = "asc", required = false) String sortDirection,
      @RequestParam(defaultValue = "0", required = false) @Min(0) Integer page,
      @RequestParam(defaultValue = "10", required = false) @Min(0) Integer size)
      throws InvalidJwtTokenException, NotFoundException {
    List<EventDto> eventDtoList =
        eventService.getAllByFilterAndGuest(search, sortBy, sortDirection, page, size);
    return buildResponse(HttpStatus.OK, eventDtoList, "Events retrieved successfully.");
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<EventDto>> getEventByIdForGuest(@PathVariable @Positive Long id)
      throws InvalidJwtTokenException, NotFoundException {
    EventDto eventDto = eventService.getByIdAndGuest(id);
    return buildResponse(HttpStatus.OK, eventDto, "Event retrieved successfully.");
  }

  @GetMapping("/{id}/photos")
  public ResponseEntity<ApiResponse<List<PhotoDto>>> getEventPhotosByEventId(
      @PathVariable @Positive Long id) throws InvalidJwtTokenException, NotFoundException {
    List<PhotoDto> photos = eventService.getGuestEventPhotosByEventId(id);
    return buildResponse(HttpStatus.OK, photos, "Photos found.");
  }

  @GetMapping("/photo")
  public ResponseEntity<ByteArrayResource> getGuestEventPhotoByUrl(@RequestParam("url") String url)
      throws InvalidJwtTokenException, NotFoundException {
    GetSinglePhotoDto getSinglePhoto = eventService.getGuestEventPhotoByUrl(url);
    ByteArrayResource resource = new ByteArrayResource(getSinglePhoto.getImageBytes());
    MediaType mediaType = MediaType.parseMediaType(getSinglePhoto.getContentType());
    String contentDisposition = "attachment; filename=\"" + getSinglePhoto.getFileName() + "\"";
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .contentLength(getSinglePhoto.getImageBytes().length)
        .contentType(mediaType)
        .body(resource);
  }
}

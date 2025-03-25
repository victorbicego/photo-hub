package com.event_manager.photo_hub.controllers.privates.host;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.CreateEventDto;
import com.event_manager.photo_hub.models.dtos.EventDto;
import com.event_manager.photo_hub.services.EventService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/host/event")
@RequiredArgsConstructor
@Validated
public class HostEventController {

  private final EventService eventService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<EventDto>>> getAllEventsForHost(
      @RequestParam(defaultValue = "", required = false) String search,
      @RequestParam(defaultValue = "name", required = false) String sortBy,
      @RequestParam(defaultValue = "asc", required = false) String sortDirection,
      @RequestParam(defaultValue = "0", required = false) @Min(0) Integer page,
      @RequestParam(defaultValue = "10", required = false) @Min(0) Integer size)
      throws InvalidJwtTokenException, NotFoundException {
    List<EventDto> eventDtoList =
        eventService.getAllByFilterAndHost(search, sortBy, sortDirection, page, size);
    return buildResponse(HttpStatus.OK, eventDtoList, "Events retrieved successfully.");
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<EventDto>> getEventByIdForHost(@PathVariable @Positive Long id)
      throws InvalidJwtTokenException, NotFoundException {
    EventDto eventDto = eventService.getByIdAndHost(id);
    return buildResponse(HttpStatus.OK, eventDto, "Event retrieved successfully.");
  }

  @PostMapping
  public ResponseEntity<ApiResponse<EventDto>> createEvent(@RequestBody CreateEventDto event)
      throws InvalidJwtTokenException, NotFoundException {
    EventDto createdEvent = eventService.createEvent(event);
    return buildResponse(HttpStatus.OK, createdEvent, "Event created.");
  }

  @DeleteMapping("/photo/{photoId}")
  public ResponseEntity<ApiResponse<Void>> deletePhoto(@PathVariable Long photoId)
      throws NotFoundException, InvalidJwtTokenException {
    eventService.deletePhoto(photoId);
    return buildResponse(HttpStatus.OK, null, "Photo deleted successfully.");
  }
}

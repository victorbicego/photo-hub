package com.event_manager.photo_hub.controllers.privates.host;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.CreateEventDto;
import com.event_manager.photo_hub.models.dtos.EventDto;
import com.event_manager.photo_hub.services.EventService;

@RestController
@RequestMapping("/api/v1/host/event")
@RequiredArgsConstructor
@Validated
public class HostEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventDto>>> getAllByFilter(
            @RequestParam(defaultValue = "", required = false) String search,
            @RequestParam(defaultValue = "name", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirection,
            @RequestParam(defaultValue = "0", required = false) @Min(0) Integer page,
            @RequestParam(defaultValue = "10", required = false) @Min(0) Integer size) {

        List<EventDto> eventDtoList =
                eventService.getAllByFilter(search, sortBy, sortDirection, page, size);

        return buildResponse(HttpStatus.OK, eventDtoList, "Events retrieved successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDto>> getById(@PathVariable @Positive Long id)
            throws NotFoundException {

        EventDto eventDto = eventService.getById(id);

        return buildResponse(HttpStatus.OK, eventDto, "Event retrieved successfully.");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventDto>> createEvent(@RequestBody CreateEventDto event) throws
            InvalidJwtTokenException, NotFoundException {

        EventDto createdEvent = eventService.createEvent(event);
        return buildResponse(HttpStatus.OK, createdEvent, "Event created.");
    }
}

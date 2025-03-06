package com.event_manager.photo_hub.controllers.privates.host;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.event_manager.photo_hub.services.EventService;

@RestController
@RequestMapping("/api/v1/host/event")
@RequiredArgsConstructor
@Validated
public class HostEventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<ApiResponse<EventDto>> createEvent(@RequestBody CreateEventDto event){

        EventDto createdEvent = eventService.createEvent(event);
        return buildResponse(HttpStatus.OK, createdEvent, "Event created.");
    }
}

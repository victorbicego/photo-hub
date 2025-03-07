package com.event_manager.photo_hub.services;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.util.List;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.CreateEventDto;
import com.event_manager.photo_hub.models.dtos.EventDto;

public interface EventService {

    EventDto createEvent(CreateEventDto event) throws InvalidJwtTokenException, NotFoundException;

    EventDto getById(Long id) throws NotFoundException;

    List<EventDto> getAllByFilter(String search, String sortBy, String sortDirection, Integer page, Integer size);
}

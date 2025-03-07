package com.event_manager.photo_hub.services.impl;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.CreateEventDto;
import com.event_manager.photo_hub.models.dtos.EventDto;
import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.models.mappers.EventMapper;
import com.event_manager.photo_hub.services.EventService;
import com.event_manager.photo_hub.services.QrCodeGeneratorService;
import com.event_manager.photo_hub.services.helpers.AuthenticationHelper;
import com.event_manager.photo_hub.services.utils.DateUtil;
import com.event_manager.photo_hub.services_crud.EventCrudService;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventCrudService eventCrudService;
    private final AuthenticationHelper authenticationHelper;
    private final EventMapper eventMapper;

    @Override
    public EventDto createEvent(CreateEventDto createEventDto) throws InvalidJwtTokenException, NotFoundException {
        DateUtil.validateDateRange(createEventDto.getStartDate(), createEventDto.getEndDate());
        Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
        Event event = eventMapper.toEntity(createEventDto);
        event.setHost(authenticatedHost);
        Event savedEvent = eventCrudService.save(event);
        // O QR code será gerado automaticamente pelo listener @PostPersist
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventDto getById(Long id) throws NotFoundException {
        Event foundEvent = eventCrudService.findById(id);
        return eventMapper.toDto(foundEvent);
    }

    @Override
    public List<EventDto> getAllByFilter(String search, String sortBy, String sortDirection, Integer page, Integer size) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return eventCrudService.findAllByFilter(search, pageable).stream()
                .map(eventMapper::toDto)
                .toList();
    }
}

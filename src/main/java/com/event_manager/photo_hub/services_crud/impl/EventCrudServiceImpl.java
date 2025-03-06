package com.event_manager.photo_hub.services_crud.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.repositories.EventRepository;
import com.event_manager.photo_hub.services_crud.EventCrudService;

@Service
@RequiredArgsConstructor
public class EventCrudServiceImpl implements EventCrudService {

    private final EventRepository eventRepository;

    @Override
    public Event findByQrCode(String eventQrCode) throws NotFoundException {
        return eventRepository
                .findByQrCode(eventQrCode)
                .orElseThrow(
                        () ->
                                new NotFoundException(
                                        String.format(
                                                "No event found with qrCode: '%s'.", eventQrCode)));
    }
}

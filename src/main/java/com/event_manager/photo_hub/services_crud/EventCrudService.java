package com.event_manager.photo_hub.services_crud;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Event;

public interface EventCrudService {

    Event findByQrCode(String eventQrCode) throws NotFoundException;

    Event save(Event event);

    Event findById(Long id) throws NotFoundException;

    Page<Event> findAllByFilter(String search, Pageable pageable);
}

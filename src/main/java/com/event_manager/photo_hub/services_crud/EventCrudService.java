package com.event_manager.photo_hub.services_crud;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Host;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventCrudService {

  Event findByQrCode(String eventQrCode) throws NotFoundException;

  Event save(Event event);

  Event findById(Long id) throws NotFoundException;

  Page<Event> findAllByFilter(String search, Pageable pageable);

  Page<Event> findAllByFilterAndHost(String search, Pageable pageable, Host authenticatedHost);

  Event findByIdAndHost(Long id, Host authenticatedHost) throws NotFoundException;
}

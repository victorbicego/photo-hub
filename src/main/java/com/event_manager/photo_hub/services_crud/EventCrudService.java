package com.event_manager.photo_hub.services_crud;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Host;

public interface EventCrudService {

  Event findByQrCode(String qrCode);

  Event save(Event event);

  Page<Event> findAllByFilterAndHost(String search, Pageable pageable, Host host);

  Event findByIdAndHost(Long id, Host host);

  void delete(Long id);
}

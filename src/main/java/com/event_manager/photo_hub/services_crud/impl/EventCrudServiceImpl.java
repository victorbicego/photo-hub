package com.event_manager.photo_hub.services_crud.impl;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.repositories.EventRepository;
import com.event_manager.photo_hub.services_crud.EventCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventCrudServiceImpl implements EventCrudService {

  private final EventRepository eventRepository;

  @Override
  public Event findByQrCode(String qrCode) {
    return eventRepository
        .findByQrCode(qrCode)
        .orElseThrow(
            () ->
                new NotFoundException(String.format("No event found with qrCode: '%s'.", qrCode)));
  }

  @Override
  public Event save(Event event) {
    return eventRepository.save(event);
  }

  @Override
  public Page<Event> findAllByFilterAndHost(String search, Pageable pageable, Host host) {
    return eventRepository.findBySearchTermAndHost(search, pageable, host);
  }

  @Override
  public Event findByIdAndHost(Long id, Host host) {
    return eventRepository
        .findByIdAndHost(id, host)
        .orElseThrow(
            () -> new NotFoundException(String.format("No Event found with id: '%s'.", id)));
  }
}

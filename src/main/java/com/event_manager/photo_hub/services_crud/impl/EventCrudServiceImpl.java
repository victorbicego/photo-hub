package com.event_manager.photo_hub.services_crud.impl;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Guest;
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
  public Event findByQrCode(String eventQrCode) throws NotFoundException {
    return eventRepository
        .findByQrCode(eventQrCode)
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format("No event found with qrCode: '%s'.", eventQrCode)));
  }

  @Override
  public Event save(Event event) {
    return eventRepository.save(event);
  }

  @Override
  public Event findById(Long id) throws NotFoundException {
    return eventRepository
        .findById(id)
        .orElseThrow(
            () -> new NotFoundException(String.format("No Event found with id: '%s'.", id)));
  }

  @Override
  public Page<Event> findAllByFilter(String search, Pageable pageable) {
    return eventRepository.findBySearchTerm(search, pageable);
  }

  @Override
  public Event findByIdAndGuest(Long id, Guest guest) throws NotFoundException {
    return eventRepository
        .findByIdAndGuest(id, guest)
        .orElseThrow(
            () -> new NotFoundException(String.format("No Event found with id: '%s'.", id)));
  }

  @Override
  public Page<Event> findAllByFilterAndGuest(String search, Pageable pageable, Guest guest) {
    return eventRepository.findBySearchTermAndGuest(search, pageable, guest);
  }

  @Override
  public Page<Event> findAllByFilterAndHost(String search, Pageable pageable, Host host) {
    return eventRepository.findBySearchTermAndHost(search, pageable, host);
  }

  @Override
  public Event findByIdAndHost(Long id, Host host) throws NotFoundException {
    return eventRepository
        .findByIdAndHost(id, host)
        .orElseThrow(
            () -> new NotFoundException(String.format("No Event found with id: '%s'.", id)));
  }
}

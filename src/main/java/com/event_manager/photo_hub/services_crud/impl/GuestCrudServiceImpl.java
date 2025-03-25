package com.event_manager.photo_hub.services_crud.impl;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Guest;
import com.event_manager.photo_hub.repositories.GuestRepository;
import com.event_manager.photo_hub.services_crud.GuestCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestCrudServiceImpl implements GuestCrudService {

  private final GuestRepository guestRepository;

  @Override
  public Guest findById(Long id) throws NotFoundException {
    return guestRepository
        .findById(id)
        .orElseThrow(
            () -> new NotFoundException(String.format("No guest found with id: '%d'.", id)));
  }

  @Override
  public Guest findByUsername(String username) throws NotFoundException {
    return guestRepository
        .findByUsername(username)
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format("No guest found with username: '%s'.", username)));
  }

  @Override
  public Guest save(Guest guest) {
    return guestRepository.save(guest);
  }

  @Override
  public Guest updatePassword(Long id, String encodedPassword) throws NotFoundException {
    Guest foundGuest = findById(id);
    foundGuest.setPassword(encodedPassword);
    return guestRepository.save(foundGuest);
  }

  @Override
  public void delete(Long id) throws NotFoundException {
    if (!guestRepository.existsById(id)) {
      throw new NotFoundException(String.format("No guest found with id: '%d'.", id));
    }
    guestRepository.deleteById(id);
  }
}

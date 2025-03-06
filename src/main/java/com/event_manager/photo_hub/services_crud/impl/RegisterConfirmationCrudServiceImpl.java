package com.event_manager.photo_hub.services_crud.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.RegisterConfirmation;
import com.event_manager.photo_hub.repositories.RegisterConfirmationRepository;
import com.event_manager.photo_hub.services_crud.RegisterConfirmationCrudService;

@Service
@RequiredArgsConstructor
public class RegisterConfirmationCrudServiceImpl implements RegisterConfirmationCrudService {

  private final RegisterConfirmationRepository registerConfirmationRepository;

  @Override
  public RegisterConfirmation findByUsername(String username) throws NotFoundException {
    return registerConfirmationRepository
        .findByUsername(username)
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format(
                        "No register confirmation found with username: '%s'.", username)));
  }

  @Override
  public RegisterConfirmation save(RegisterConfirmation registerConfirmation) {
    return registerConfirmationRepository.save(registerConfirmation);
  }

  @Override
  public void delete(Long id) throws NotFoundException {
    if (!registerConfirmationRepository.existsById(id)) {
      throw new NotFoundException(
          String.format("No register confirmation found with id: '%d'.", id));
    }
    registerConfirmationRepository.deleteById(id);
  }
}

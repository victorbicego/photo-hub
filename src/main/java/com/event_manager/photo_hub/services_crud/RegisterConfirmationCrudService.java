package com.event_manager.photo_hub.services_crud;

import com.event_manager.photo_hub.models.entities.RegisterConfirmation;

public interface RegisterConfirmationCrudService {

  RegisterConfirmation findByUsername(String username);

  RegisterConfirmation save(RegisterConfirmation registerConfirmation);

  void delete(Long id);
}

package com.event_manager.photo_hub.services_crud;

import com.event_manager.photo_hub.models.entities.Host;

public interface HostCrudService {

  Host findById(Long id);

  Host findByUsername(String username);

  Host save(Host host);

  Host updatePassword(Long id, String password);

  void delete(Long id);
}

package com.event_manager.photo_hub.services_crud;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Host;

public interface HostCrudService {

    Host findById(Long id) throws NotFoundException;

    Host findByUsername(String username) throws NotFoundException;

    Host save(Host host);

    Host updatePassword(Long id, String encodedPassword) throws NotFoundException;

    void delete(Long id) throws NotFoundException;

    boolean existsById(Long id);
}

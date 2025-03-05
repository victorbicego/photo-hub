package com.event_manager.photo_hub.services_crud;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Guest;

public interface GuestCrudService {

    Guest findById(Long id) throws NotFoundException;

    Guest findByUsername(String username) throws NotFoundException;

    Guest save(Guest guest);

    Guest updatePassword(Long id, String encodedPassword) throws NotFoundException;

    void delete(Long id) throws NotFoundException;

    boolean existsById(Long id);
}

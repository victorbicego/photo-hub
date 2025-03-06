package com.event_manager.photo_hub.services_crud;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Event;

public interface EventCrudService {

    Event findByQrCode(String eventQrCode) throws NotFoundException;
}

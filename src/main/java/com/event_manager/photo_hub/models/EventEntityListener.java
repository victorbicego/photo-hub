package com.event_manager.photo_hub.models;

import jakarta.persistence.PostPersist;

import org.springframework.stereotype.Component;

import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.services.QrCodeGeneratorService;
import com.event_manager.photo_hub.services_crud.EventCrudService;

@Component
public class EventEntityListener {

    private final QrCodeGeneratorService qrCodeGeneratorService;
    private final EventCrudService eventCrudService;

    public EventEntityListener(QrCodeGeneratorService qrCodeGeneratorService, EventCrudService eventCrudService) {
        this.qrCodeGeneratorService = qrCodeGeneratorService;
        this.eventCrudService = eventCrudService;
    }

    @PostPersist
    public void generateQrCode(Event event) {
        String qrCode = qrCodeGeneratorService.generateQrCode(event.getId().toString());
        event.setQrCode(qrCode);
        eventCrudService.save(event);
    }
}

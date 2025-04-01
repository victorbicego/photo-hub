package com.event_manager.photo_hub.services;

import jakarta.mail.MessagingException;

import com.event_manager.photo_hub.models.dtos.CreateHostDto;
import com.event_manager.photo_hub.models.dtos.HostDto;

public interface RegisterService {

    HostDto registerHost(CreateHostDto createHostDto) throws MessagingException;
}

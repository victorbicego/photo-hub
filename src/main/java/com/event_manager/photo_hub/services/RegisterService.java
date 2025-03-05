package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.models.dtos.CreateGuestDto;
import com.event_manager.photo_hub.models.dtos.CreateHostDto;
import com.event_manager.photo_hub.models.dtos.GuestDto;
import com.event_manager.photo_hub.models.dtos.HostDto;
import jakarta.validation.Valid;

public interface RegisterService {

    HostDto registerHost(CreateHostDto createHostDto);

    GuestDto registerGuest(CreateGuestDto createGuestDto);
}

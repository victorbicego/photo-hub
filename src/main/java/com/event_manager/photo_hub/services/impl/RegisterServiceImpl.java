package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.models.dtos.CreateGuestDto;
import com.event_manager.photo_hub.models.dtos.CreateHostDto;
import com.event_manager.photo_hub.models.dtos.GuestDto;
import com.event_manager.photo_hub.models.dtos.HostDto;
import com.event_manager.photo_hub.services.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    @Override
    public HostDto registerHost(CreateHostDto createHostDto) {
        return null;
    }

    @Override
    public GuestDto registerGuest(CreateGuestDto createGuestDto) {
        return null;
    }
}

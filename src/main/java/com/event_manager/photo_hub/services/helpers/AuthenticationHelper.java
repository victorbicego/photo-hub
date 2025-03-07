package com.event_manager.photo_hub.services.helpers;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Guest;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.services.JwtService;
import com.event_manager.photo_hub.services_crud.GuestCrudService;
import com.event_manager.photo_hub.services_crud.HostCrudService;

@Service
@RequiredArgsConstructor
public class AuthenticationHelper {

    private final JwtService jwtService;
    private final HostCrudService hostCrudService;
    private final GuestCrudService guestCrudService;

    public Host getAuthenticatedHost() throws NotFoundException, InvalidJwtTokenException {
        String username = jwtService.getActiveUsername();
        return hostCrudService.findByUsername(username);
    }

    public Guest getAuthenticatedGuest() throws NotFoundException, InvalidJwtTokenException {
        String username = jwtService.getActiveUsername();
        return guestCrudService.findByUsername(username);
    }
}

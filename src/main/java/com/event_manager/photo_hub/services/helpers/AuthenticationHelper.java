package com.event_manager.photo_hub.services.helpers;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.services.JwtService;
import com.event_manager.photo_hub.services_crud.EventCrudService;
import com.event_manager.photo_hub.services_crud.HostCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationHelper {

  private final JwtService jwtService;
  private final HostCrudService hostCrudService;
  private final EventCrudService eventCrudService;

  public Host getAuthenticatedHost() throws NotFoundException, InvalidJwtTokenException {
    String username = jwtService.getActiveUsername();
    return hostCrudService.findByUsername(username);
  }

  public Event getActiveEvent() throws InvalidJwtTokenException, NotFoundException {
    String qrCode = jwtService.getActiveQrCode();
    return eventCrudService.findByQrCode(qrCode);
  }
}

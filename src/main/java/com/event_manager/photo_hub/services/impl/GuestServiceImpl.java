package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.GuestDto;
import com.event_manager.photo_hub.models.dtos.PasswordDto;
import com.event_manager.photo_hub.models.dtos.UpdateGuestDto;
import com.event_manager.photo_hub.models.entities.Guest;
import com.event_manager.photo_hub.models.mappers.GuestMapper;
import com.event_manager.photo_hub.services.GuestService;
import com.event_manager.photo_hub.services.helpers.AuthenticationHelper;
import com.event_manager.photo_hub.services_crud.GuestCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

  private final AuthenticationHelper authenticationHelper;
  private final GuestMapper guestMapper;
  private final GuestCrudService guestCrudService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public GuestDto getInfo() throws InvalidJwtTokenException, NotFoundException {
    Guest authenticatedGuest = authenticationHelper.getAuthenticatedGuest();
    return guestMapper.toDto(authenticatedGuest);
  }

  @Override
  public GuestDto update(UpdateGuestDto updateGuestDto)
      throws InvalidJwtTokenException, NotFoundException {
    Guest authenticatedGuest = authenticationHelper.getAuthenticatedGuest();
    authenticatedGuest.setFirstName(updateGuestDto.getFirstName());
    authenticatedGuest.setLastName(updateGuestDto.getLastName());
    Guest savedGuest = guestCrudService.save(authenticatedGuest);
    return guestMapper.toDto(savedGuest);
  }

  @Override
  public GuestDto updatePassword(PasswordDto updatePasswordDto)
      throws InvalidJwtTokenException, NotFoundException {
    Guest authenticatedGuest = authenticationHelper.getAuthenticatedGuest();
    Guest savedGuest =
        guestCrudService.updatePassword(
            authenticatedGuest.getId(), passwordEncoder.encode(updatePasswordDto.getPassword()));
    return guestMapper.toDto(savedGuest);
  }

  @Override
  public void delete() throws InvalidJwtTokenException, NotFoundException {
    Guest authenticatedGuest = authenticationHelper.getAuthenticatedGuest();
    guestCrudService.delete(authenticatedGuest.getId());
  }
}

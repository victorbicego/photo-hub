package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.GuestDto;
import com.event_manager.photo_hub.models.dtos.PasswordDto;
import com.event_manager.photo_hub.models.dtos.UpdateGuestDto;

public interface GuestService {

  GuestDto getInfo() throws InvalidJwtTokenException, NotFoundException;

  GuestDto update(UpdateGuestDto updateGuestDto) throws InvalidJwtTokenException, NotFoundException;

  GuestDto updatePassword(PasswordDto updatePasswordDto)
      throws InvalidJwtTokenException, NotFoundException;

  void delete() throws InvalidJwtTokenException, NotFoundException;
}

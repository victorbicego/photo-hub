package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.HostDto;
import com.event_manager.photo_hub.models.dtos.PasswordDto;
import com.event_manager.photo_hub.models.dtos.UpdateHostDto;

public interface HostService {

  HostDto getInfo() throws InvalidJwtTokenException, NotFoundException;

  HostDto update(UpdateHostDto updateConsumerDto)
      throws InvalidJwtTokenException, NotFoundException;

  HostDto updatePassword(PasswordDto updatePasswordDto)
      throws InvalidJwtTokenException, NotFoundException;

  void delete() throws InvalidJwtTokenException, NotFoundException;
}

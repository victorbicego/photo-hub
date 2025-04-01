package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.models.dtos.HostDto;
import com.event_manager.photo_hub.models.dtos.PasswordDto;
import com.event_manager.photo_hub.models.dtos.UpdateHostDto;

public interface HostService {

  HostDto get();

  HostDto update(UpdateHostDto updateConsumerDto);

  HostDto updatePassword(PasswordDto updatePasswordDto);

  void delete();
}

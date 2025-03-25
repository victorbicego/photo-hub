package com.event_manager.photo_hub.models.mappers;

import com.event_manager.photo_hub.models.dtos.CreateGuestDto;
import com.event_manager.photo_hub.models.dtos.GuestDto;
import com.event_manager.photo_hub.models.entities.Guest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestMapper {

  private final ModelMapper modelMapper;

  public GuestDto toDto(Guest guest) {
    return modelMapper.map(guest, GuestDto.class);
  }

  public Guest toEntity(CreateGuestDto createGuestDto) {
    return modelMapper.map(createGuestDto, Guest.class);
  }
}

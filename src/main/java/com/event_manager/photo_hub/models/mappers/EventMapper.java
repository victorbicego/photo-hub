package com.event_manager.photo_hub.models.mappers;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.models.dtos.CreateEventDto;
import com.event_manager.photo_hub.models.dtos.CreateGuestDto;
import com.event_manager.photo_hub.models.dtos.EventDto;
import com.event_manager.photo_hub.models.dtos.GuestDto;
import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Guest;

@Service
@RequiredArgsConstructor
public class EventMapper {

    private final ModelMapper modelMapper;

    public EventDto toDto(Event event) {
        return modelMapper.map(event, EventDto.class);
    }

    public Event toEntity(CreateEventDto createEventDto) {
        return modelMapper.map(createEventDto, Event.class);
    }
}

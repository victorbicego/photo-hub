package com.event_manager.photo_hub.models.mappers;

import com.event_manager.photo_hub.models.dtos.CreateEventDto;
import com.event_manager.photo_hub.models.dtos.EventDto;
import com.event_manager.photo_hub.models.entities.Event;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventMapper {

  private final ModelMapper modelMapper;
  private final HostMapper hostMapper;

  public EventDto toDto(Event event) {
    EventDto eventDto = modelMapper.map(event, EventDto.class);
    if (event.getCoHosts() != null) {
      eventDto.setCoHosts(event.getCoHosts().stream().map(hostMapper::toDto).toList());
    }
    return eventDto;
  }

  public Event toEntity(CreateEventDto createEventDto) {
    return modelMapper.map(createEventDto, Event.class);
  }
}

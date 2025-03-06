package com.event_manager.photo_hub.models.mappers;

import com.event_manager.photo_hub.models.dtos.CreateHostDto;
import com.event_manager.photo_hub.models.dtos.HostDto;
import com.event_manager.photo_hub.models.entities.Host;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HostMapper {

    private final ModelMapper modelMapper;

    public HostDto toDto(Host host) {
        return modelMapper.map(host, HostDto.class);
    }

    public Host toEntity(CreateHostDto createHostDto) {
        return modelMapper.map(createHostDto, Host.class);
    }
}

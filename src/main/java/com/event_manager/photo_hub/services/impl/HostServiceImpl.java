package com.event_manager.photo_hub.services.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.HostDto;
import com.event_manager.photo_hub.models.dtos.PasswordDto;
import com.event_manager.photo_hub.models.dtos.UpdateHostDto;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.models.mappers.EventMapper;
import com.event_manager.photo_hub.models.mappers.HostMapper;
import com.event_manager.photo_hub.services.HostService;
import com.event_manager.photo_hub.services.helpers.AuthenticationHelper;
import com.event_manager.photo_hub.services_crud.HostCrudService;

@Service
@RequiredArgsConstructor
public class HostServiceImpl implements HostService {

    private final AuthenticationHelper authenticationHelper;
    private final HostMapper hostMapper;
    private final HostCrudService hostCrudService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public HostDto getInfo() throws InvalidJwtTokenException, NotFoundException {
        Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
        return hostMapper.toDto(authenticatedHost);
    }

    @Override
    public HostDto update(UpdateHostDto updateConsumerDto) throws InvalidJwtTokenException, NotFoundException {
        Host authenticatedHost = authenticationHelper.getAuthenticatedHost();




        Host savedHost = hostCrudService.save(authenticatedHost);
        return hostMapper.toDto(savedHost);
    }

    @Override
    public HostDto updatePassword(PasswordDto updatePasswordDto) throws InvalidJwtTokenException, NotFoundException {
        Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
        authenticatedHost.setPassword(passwordEncoder.encode(updatePasswordDto.getPassword()));
        Host savedHost = hostCrudService.save(authenticatedHost);
        return hostMapper.toDto(savedHost);
    }

    @Override
    public void delete() throws InvalidJwtTokenException, NotFoundException {
        Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
        hostCrudService.delete(authenticatedHost.getId());
    }
}

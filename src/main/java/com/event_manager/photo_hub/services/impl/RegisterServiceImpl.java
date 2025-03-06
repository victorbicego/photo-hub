package com.event_manager.photo_hub.services.impl;

import static com.event_manager.photo_hub.services.utils.CodeGeneratorUtil.generateRandomCode;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.CreateGuestDto;
import com.event_manager.photo_hub.models.dtos.CreateHostDto;
import com.event_manager.photo_hub.models.dtos.GuestDto;
import com.event_manager.photo_hub.models.dtos.HostDto;
import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Guest;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.models.entities.RegisterConfirmation;
import com.event_manager.photo_hub.models.mappers.GuestMapper;
import com.event_manager.photo_hub.models.mappers.HostMapper;
import com.event_manager.photo_hub.services.EmailService;
import com.event_manager.photo_hub.services.RegisterService;
import com.event_manager.photo_hub.services_crud.EventCrudService;
import com.event_manager.photo_hub.services_crud.GuestCrudService;
import com.event_manager.photo_hub.services_crud.HostCrudService;
import com.event_manager.photo_hub.services_crud.RegisterConfirmationCrudService;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private static final int CONFIRMATION_CODE_LENGTH = 10;
    private static final int CODE_EXPIRY_MINUTES = 60;

    private final HostCrudService hostCrudService;
    private final GuestCrudService guestCrudService;
    private final RegisterConfirmationCrudService registerConfirmationCrudService;
    private final EmailService emailService;
    private final HostMapper hostMapper;
    private final GuestMapper guestMapper;
    private final PasswordEncoder passwordEncoder;
    private final EventCrudService eventCrudService;

    @Override
    public HostDto registerHost(CreateHostDto createHostDto) throws MessagingException {
        Host mappedHost = mapToEntity(createHostDto);
        Host savedHost = hostCrudService.save(mappedHost);
        sendRegisterConfirmation(savedHost.getUsername());
        return hostMapper.toDto(savedHost);
    }

    @Override
    public GuestDto registerGuest(CreateGuestDto createGuestDto) throws MessagingException, NotFoundException {
        Event event = eventCrudService.findByQrCode(createGuestDto.getEventQrCode());
        Guest mappedGuest = mapToEntity(createGuestDto);
        mappedGuest.getEvents().add(event);
        Guest savedGuest = guestCrudService.save(mappedGuest);
        sendRegisterConfirmation(savedGuest.getUsername());
        return guestMapper.toDto(savedGuest);
    }

    private Host mapToEntity(CreateHostDto createHostDto) {
        Host host = hostMapper.toEntity(createHostDto);
        host.setPassword(passwordEncoder.encode(createHostDto.getPassword()));
        host.setEnabled(false);
        return host;
    }

    private Guest mapToEntity(CreateGuestDto createGuestDto) {
        Guest guest = guestMapper.toEntity(createGuestDto);
        guest.setPassword(passwordEncoder.encode(createGuestDto.getPassword()));
        guest.setEnabled(false);
        return guest;
    }

    private void sendRegisterConfirmation(String username) throws MessagingException {
        RegisterConfirmation confirmation = createRegisterConfirmation(username);
        emailService.sendConfirmationCode(confirmation.getUsername(), confirmation.getCode());
    }

    private RegisterConfirmation createRegisterConfirmation(String username) {
        String code = generateRandomCode(CONFIRMATION_CODE_LENGTH);
        RegisterConfirmation confirmation = new RegisterConfirmation(null, username, code, LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
        return registerConfirmationCrudService.save(confirmation);
    }
}

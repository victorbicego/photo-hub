package com.event_manager.photo_hub.services.impl;

import static com.event_manager.photo_hub.services.utils.CodeGeneratorUtil.generateRandomCode;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.models.dtos.CreateHostDto;
import com.event_manager.photo_hub.models.dtos.HostDto;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.models.entities.RegisterConfirmation;
import com.event_manager.photo_hub.models.mappers.HostMapper;
import com.event_manager.photo_hub.services.EmailService;
import com.event_manager.photo_hub.services.RegisterService;
import com.event_manager.photo_hub.services_crud.HostCrudService;
import com.event_manager.photo_hub.services_crud.RegisterConfirmationCrudService;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

  private static final int CONFIRMATION_CODE_LENGTH = 10;
  private static final int CODE_EXPIRY_MINUTES = 60;

  private final HostCrudService hostCrudService;
  private final RegisterConfirmationCrudService registerConfirmationCrudService;
  private final EmailService emailService;
  private final HostMapper hostMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public HostDto registerHost(CreateHostDto createHostDto) throws MessagingException {
    Host host = convertToHostEntity(createHostDto);
    Host savedHost = hostCrudService.save(host);
    sendRegisterConfirmation(savedHost.getUsername());
    return hostMapper.toDto(savedHost);
  }

  private Host convertToHostEntity(CreateHostDto createHostDto) {
    Host host = hostMapper.toEntity(createHostDto);
    host.setPassword(passwordEncoder.encode(createHostDto.getPassword()));
    host.setEnabled(false);
    return host;
  }

  private void sendRegisterConfirmation(String username) throws MessagingException {
    RegisterConfirmation confirmation = createRegisterConfirmation(username);
    emailService.sendConfirmationCode(confirmation.getUsername(), confirmation.getCode());
  }

  private RegisterConfirmation createRegisterConfirmation(String username) {
    String code = generateRandomCode(CONFIRMATION_CODE_LENGTH);
    LocalDateTime expiry = ZonedDateTime.now(ZoneId.systemDefault())
            .toLocalDateTime()
            .plusMinutes(CODE_EXPIRY_MINUTES);
    RegisterConfirmation registerConfirmation =
        new RegisterConfirmation(
                null, username, code, expiry);
    return registerConfirmationCrudService.save(registerConfirmation);
  }
}

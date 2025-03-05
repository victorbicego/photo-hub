package com.event_manager.photo_hub.services.impl;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.ExpiredRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.InvalidRegistrationCodeException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.Role;
import com.event_manager.photo_hub.models.dtos.EmailDto;
import com.event_manager.photo_hub.models.dtos.LoginResponseDto;
import com.event_manager.photo_hub.models.dtos.RegisterConfirmationDto;
import com.event_manager.photo_hub.models.entities.RegisterConfirmation;
import com.event_manager.photo_hub.services.EmailService;
import com.event_manager.photo_hub.services.JwtService;
import com.event_manager.photo_hub.services.RegisterConfirmationService;
import com.event_manager.photo_hub.services.UserService;
import com.event_manager.photo_hub.services.utils.RoleUtil;
import com.event_manager.photo_hub.services_crud.RegisterConfirmationCrudService;

@Service
@RequiredArgsConstructor
public class RegisterConfirmationServiceImpl implements RegisterConfirmationService {

    private static final int CODE_EXPIRY_MINUTES = 60;

    private final RegisterConfirmationCrudService registerConfirmationCrudService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserService userService;

    @Override
    public LoginResponseDto confirmRegistration(RegisterConfirmationDto confirmationDto)
            throws NotFoundException, ExpiredRegistrationCodeException, InvalidRegistrationCodeException,
            BadRequestException {
        RegisterConfirmation foundRegisterConfirmation = registerConfirmationCrudService.findByUsername(confirmationDto.getUsername());
        validateRegisterConfirmation(foundRegisterConfirmation, confirmationDto);

        UserDetails activatedUser = userService.activateUser(foundRegisterConfirmation.getUsername());
        registerConfirmationCrudService.delete(foundRegisterConfirmation.getId());
        Cookie cookie = jwtService.createCookie(activatedUser);
        Role role = RoleUtil.determineRole(activatedUser);
        return new LoginResponseDto(cookie, activatedUser.getUsername(), role);
    }

    private void validateRegisterConfirmation(
            RegisterConfirmation confirmation, RegisterConfirmationDto dto)
            throws InvalidRegistrationCodeException, ExpiredRegistrationCodeException {
        if (!confirmation.getCode().equals(dto.getCode())) {
            throw new InvalidRegistrationCodeException("Invalid confirmation code.");
        }

        if (LocalDateTime.now().isAfter(confirmation.getExpiryDate())) {
            throw new ExpiredRegistrationCodeException("Confirmation code has expired.");
        }
    }

    @Override
    public void resendConfirmationCode(EmailDto emailDto)
            throws NotFoundException, MessagingException {
        RegisterConfirmation foundRegisterConfirmation = registerConfirmationCrudService.findByUsername(emailDto.getUsername());
        RegisterConfirmation updatedRegisterConfirmation = updateExpiryDate(foundRegisterConfirmation);
        emailService.resendConfirmationCode(
                updatedRegisterConfirmation.getUsername(), updatedRegisterConfirmation.getCode());
    }

    private RegisterConfirmation updateExpiryDate(RegisterConfirmation confirmation) {
        confirmation.setExpiryDate(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
        return registerConfirmationCrudService.save(confirmation);
    }
}

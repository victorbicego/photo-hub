package com.event_manager.photo_hub.services.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.models.Role;
import com.event_manager.photo_hub.models.entities.Guest;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.services.UserService;
import com.event_manager.photo_hub.services.utils.RoleUtil;
import com.event_manager.photo_hub.services_crud.GuestCrudService;
import com.event_manager.photo_hub.services_crud.HostCrudService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CustomUserDetailsService customUserDetailsService;
    private final HostCrudService hostCrudService;
    private final GuestCrudService guestCrudService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails activateUser(String username) throws BadRequestException {
        UserDetails user = customUserDetailsService.loadUserByUsername(username);
        Role role = RoleUtil.determineRole(user);
        if (role == Role.HOST) {
            Host host = (Host) user;
            host.setEnabled(true);
            return hostCrudService.save(host);
        } else if (role == Role.GUEST) {
            Guest guest = (Guest) user;
            guest.setEnabled(true);
            return guestCrudService.save(guest);
        } else {
            throw new BadRequestException("Invalid user role.");
        }
    }

    @Override
    public void updatePassword(String username, String newPassword) throws BadRequestException {
        UserDetails user = customUserDetailsService.loadUserByUsername(username);
        Role role = RoleUtil.determineRole(user);
        if (role == Role.HOST) {
            Host host = (Host) user;
            host.setPassword(passwordEncoder.encode(newPassword));
            hostCrudService.save(host);
        } else if (role == Role.GUEST) {
            Guest guest = (Guest) user;
            guest.setPassword(passwordEncoder.encode(newPassword));
            guestCrudService.save(guest);
        } else {
            throw new BadRequestException("Invalid user role.");
        }
    }
}

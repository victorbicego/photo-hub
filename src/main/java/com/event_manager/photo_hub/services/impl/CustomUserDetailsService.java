package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.repositories.BaseUserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final List<BaseUserRepository<? extends UserDetails>> userRepositories;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepositories.stream()
        .map(repo -> repo.findByUsername(username))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseThrow(
            () ->
                new UsernameNotFoundException(
                    String.format("User with username '%s' not found.", username)));
  }
}

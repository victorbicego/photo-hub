package com.event_manager.photo_hub.repositories;

import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;

public interface BaseUserRepository<T extends UserDetails> {

  Optional<T> findByUsername(String username);
}

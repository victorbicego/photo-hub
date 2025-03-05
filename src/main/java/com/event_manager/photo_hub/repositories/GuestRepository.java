package com.event_manager.photo_hub.repositories;

import com.event_manager.photo_hub.models.entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long>, BaseUserRepository<Guest> {}

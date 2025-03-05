package com.event_manager.photo_hub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.event_manager.photo_hub.models.entities.Host;

@Repository
public interface HostRepository
        extends JpaRepository<Host, Long>, BaseUserRepository<Host> {

}

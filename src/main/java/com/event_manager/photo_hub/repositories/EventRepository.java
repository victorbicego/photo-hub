package com.event_manager.photo_hub.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.event_manager.photo_hub.models.entities.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByQrCode(String eventQrCode);
}

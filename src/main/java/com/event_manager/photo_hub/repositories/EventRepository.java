package com.event_manager.photo_hub.repositories;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.event_manager.photo_hub.models.entities.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByQrCode(String eventQrCode);

    @Query(
            "SELECT d FROM Event d "
                    + "WHERE (:searchTerm IS NULL OR "
                    + "LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Event> findBySearchTerm(String searchTerm, Pageable pageable);
}

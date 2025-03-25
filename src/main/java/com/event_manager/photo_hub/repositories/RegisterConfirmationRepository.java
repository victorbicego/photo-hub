package com.event_manager.photo_hub.repositories;

import com.event_manager.photo_hub.models.entities.RegisterConfirmation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterConfirmationRepository extends JpaRepository<RegisterConfirmation, Long> {

  Optional<RegisterConfirmation> findByUsername(String username);
}

package com.event_manager.photo_hub.repositories;

import com.event_manager.photo_hub.models.entities.ResetPasswordRequest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRequestRepository extends JpaRepository<ResetPasswordRequest, Long> {

    Optional<ResetPasswordRequest> findByUsername(String username);

    Optional<ResetPasswordRequest> findByCodeAndUsername(String code, String username);
}

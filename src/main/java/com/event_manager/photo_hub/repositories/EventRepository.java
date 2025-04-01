package com.event_manager.photo_hub.repositories;

import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Host;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

  Optional<Event> findByQrCode(String qrCode);

  Optional<Event> findByIdAndHost(Long id, Host host);

  @Query(
      "SELECT e FROM Event e WHERE e.host = :host AND (:searchTerm IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
  Page<Event> findBySearchTermAndHost(
      @Param("searchTerm") String searchTerm, Pageable pageable, @Param("host") Host host);
}

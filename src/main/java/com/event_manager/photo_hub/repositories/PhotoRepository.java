package com.event_manager.photo_hub.repositories;

import com.event_manager.photo_hub.models.entities.Photo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
  Optional<Photo> findByPhotoUrl(String url);
}

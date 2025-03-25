package com.event_manager.photo_hub.services_crud.impl;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Photo;
import com.event_manager.photo_hub.repositories.PhotoRepository;
import com.event_manager.photo_hub.services_crud.PhotoCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotoCrudServiceImpl implements PhotoCrudService {

  private final PhotoRepository photoRepository;

  @Override
  public Photo savePhoto(Photo photo) {
    return photoRepository.save(photo);
  }

  @Override
  public void delete(Long id) throws NotFoundException {
    if (!photoRepository.existsById(id)) {
      throw new NotFoundException(String.format("No photo found with id: '%d'.", id));
    }
    photoRepository.deleteById(id);
  }
}

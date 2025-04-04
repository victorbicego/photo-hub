package com.event_manager.photo_hub.services_crud;

import com.event_manager.photo_hub.models.entities.Photo;

public interface PhotoCrudService {

  Photo savePhoto(Photo photo);

  void delete(Long id);

  Photo findByUrl(String url);
}

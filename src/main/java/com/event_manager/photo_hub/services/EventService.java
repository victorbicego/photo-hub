package com.event_manager.photo_hub.services;

import com.drew.imaging.ImageProcessingException;
import com.event_manager.photo_hub.models.dtos.*;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {

  List<EventDto> getAllByFilter(
      String search, String sortBy, String sortDirection, Integer page, Integer size);

  EventDto getById(Long id);

  EventDto create(CreateEventDto event);

  EventDto update(Long id, UpdateEventDto updateEventDto);

  void delete(Long id);

  EventDto getActiveEvent();

  List<PhotoDto> getPhotosByActiveEvent();

  List<PhotoDto> getPhotosByEventId(Long id);

  PhotoDto uploadPhoto(Long id, MultipartFile file) throws IOException, ImageProcessingException;

  PhotoDto uploadPhoto(MultipartFile file, String clientIp)
      throws IOException, ImageProcessingException;

  List<PhotoRecognitionDto> getMatchedPhotos(MultipartFile file);

  GetSinglePhotoDto getPhotoByUrl(String url);

  GetSinglePhotoDto getHostEventPhotoByUrl(String url);

  DownloadPhotosDto downloadPhotos(Long id, PhotoListDto photoListDto);

  DownloadPhotosDto downloadPhotosByActiveEvent(PhotoListDto photoListDto);

  void deletePhotos(Long id, PhotoListDto photoListDto);

  void blockIp(Long id, PhotoListDto photoListDto);

  EventDto addCoHosts(Long id, CoHostListDto coHostListDto);

  HostDto getHostByUsername(String username);
}

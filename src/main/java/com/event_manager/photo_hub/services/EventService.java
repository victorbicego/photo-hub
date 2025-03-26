package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.*;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {

  EventDto createEvent(CreateEventDto event) throws InvalidJwtTokenException, NotFoundException;

  EventDto getByIdAndHost(Long id) throws NotFoundException, InvalidJwtTokenException;

  List<EventDto> getAllByFilterAndHost(
      String search, String sortBy, String sortDirection, Integer page, Integer size)
      throws InvalidJwtTokenException, NotFoundException;

  List<EventDto> getAllByFilterAndGuest(
      String search, String sortBy, String sortDirection, Integer page, Integer size)
      throws InvalidJwtTokenException, NotFoundException;

  EventDto getByIdAndGuest(Long id) throws InvalidJwtTokenException, NotFoundException;

  PhotoDto uploadPhoto(MultipartFile file) throws Exception;

  List<PhotoRecognitionDto> getMatchedPhotos(MultipartFile file)
      throws InvalidJwtTokenException, NotFoundException, IOException;

  List<PhotoDto> getPhotosByEvent() throws InvalidJwtTokenException, NotFoundException;

  GetSinglePhotoDto getPhotoByUrl(String url) throws InvalidJwtTokenException, NotFoundException;

  void deletePhoto(Long photoId) throws NotFoundException, InvalidJwtTokenException;

  DownloadPhotosDto downloadEventPhotos() throws InvalidJwtTokenException, NotFoundException;

  EventDto getActiveEvent() throws InvalidJwtTokenException, NotFoundException;

  DownloadPhotosDto downloadEventMatchPhotos(List<Long> photoIds)
      throws InvalidJwtTokenException, NotFoundException;

  List<PhotoDto> getHostEventPhotosByEventId(Long id)
      throws InvalidJwtTokenException, NotFoundException;

  List<PhotoDto> getGuestEventPhotosByEventId(Long id)
      throws InvalidJwtTokenException, NotFoundException;

  GetSinglePhotoDto getGuestEventPhotoByUrl(String url)
      throws InvalidJwtTokenException, NotFoundException;

  GetSinglePhotoDto getHostEventPhotoByUrl(String url)
      throws InvalidJwtTokenException, NotFoundException;
}

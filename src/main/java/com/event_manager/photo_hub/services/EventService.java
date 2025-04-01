package com.event_manager.photo_hub.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.CreateEventDto;
import com.event_manager.photo_hub.models.dtos.DownloadPhotosDto;
import com.event_manager.photo_hub.models.dtos.EventDto;
import com.event_manager.photo_hub.models.dtos.GetSinglePhotoDto;
import com.event_manager.photo_hub.models.dtos.PhotoDto;
import com.event_manager.photo_hub.models.dtos.PhotoListDto;
import com.event_manager.photo_hub.models.dtos.PhotoRecognitionDto;
import com.event_manager.photo_hub.models.dtos.UpdateEventDto;

public interface EventService {

    EventDto create(CreateEventDto event) throws InvalidJwtTokenException, NotFoundException;

    EventDto getById(Long id) throws NotFoundException, InvalidJwtTokenException;

    List<EventDto> getAllByFilter(
            String search, String sortBy, String sortDirection, Integer page, Integer size)
            throws InvalidJwtTokenException, NotFoundException;

    PhotoDto uploadPhoto(MultipartFile file);

    List<PhotoRecognitionDto> getMatchedPhotos(MultipartFile file)
            throws InvalidJwtTokenException, NotFoundException, IOException;

    List<PhotoDto> getPhotosByEvent() throws InvalidJwtTokenException, NotFoundException;

    GetSinglePhotoDto getPhotoByUrl(String url) throws InvalidJwtTokenException, NotFoundException;

    EventDto getActiveEvent() throws InvalidJwtTokenException, NotFoundException;

    List<PhotoDto> getPhotosByEventId(Long id)
            throws InvalidJwtTokenException, NotFoundException;

    GetSinglePhotoDto getHostEventPhotoByUrl(String url)
            throws InvalidJwtTokenException, NotFoundException;

    void deletePhotos(Long id, PhotoListDto photoListDto)
            throws InvalidJwtTokenException, NotFoundException;

    PhotoDto uploadPhoto(Long id, MultipartFile file);

    DownloadPhotosDto downloadPhotos(Long id, PhotoListDto photoListDto)
            throws NotFoundException, InvalidJwtTokenException;

    DownloadPhotosDto downloadSelectedPhotosFromActiveEvent(PhotoListDto photoListDto) throws InvalidJwtTokenException,
            NotFoundException;

    EventDto update(Long id, UpdateEventDto updateEventDto) throws NotFoundException, InvalidJwtTokenException;
}

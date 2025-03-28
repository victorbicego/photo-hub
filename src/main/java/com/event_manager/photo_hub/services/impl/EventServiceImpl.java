package com.event_manager.photo_hub.services.impl;

import static com.event_manager.photo_hub.services.utils.DateUtil.validateDateRange;
import static com.event_manager.photo_hub.services.utils.MetadataUtil.extractPhotoTakenDate;
import static com.event_manager.photo_hub.services.utils.QrCodeGeneratorUtil.generateQrCode;
import static com.event_manager.photo_hub.services.utils.StringUtil.normalizeFolderName;

import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.*;
import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Guest;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.models.entities.Photo;
import com.event_manager.photo_hub.models.mappers.EventMapper;
import com.event_manager.photo_hub.models.mappers.PhotoMapper;
import com.event_manager.photo_hub.services.EventService;
import com.event_manager.photo_hub.services.PhotoMatchingService;
import com.event_manager.photo_hub.services.StorageService;
import com.event_manager.photo_hub.services.helpers.AuthenticationHelper;
import com.event_manager.photo_hub.services_crud.EventCrudService;
import com.event_manager.photo_hub.services_crud.PhotoCrudService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventCrudService eventCrudService;
  private final AuthenticationHelper authenticationHelper;
  private final EventMapper eventMapper;
  private final PhotoMapper photoMapper;
  private final StorageService storageService;
  private final PhotoCrudService photoCrudService;
  private final PhotoMatchingService photoMatchingService;

  @Override
  public EventDto createEvent(CreateEventDto createEventDto)
      throws InvalidJwtTokenException, NotFoundException {
    validateDateRange(createEventDto.getStartDate(), createEventDto.getEndDate());
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event event = eventMapper.toEntity(createEventDto);
    event.setHost(authenticatedHost);

    String uniqueData = event.getName() + "_" + System.currentTimeMillis();
    String qrCode = generateQrCode(uniqueData);
    event.setQrCode(qrCode);

    Event savedEvent = eventCrudService.save(event);
    return eventMapper.toDto(savedEvent);
  }

  @Override
  public EventDto getByIdAndHost(Long id) throws NotFoundException, InvalidJwtTokenException {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);
    return eventMapper.toDto(foundEvent);
  }

  @Override
  public List<EventDto> getAllByFilterAndHost(
      String search, String sortBy, String sortDirection, Integer page, Integer size)
      throws InvalidJwtTokenException, NotFoundException {
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    return eventCrudService.findAllByFilterAndHost(search, pageable, authenticatedHost).stream()
        .map(eventMapper::toDto)
        .toList();
  }

  @Override
  public EventDto getByIdAndGuest(Long id) throws InvalidJwtTokenException, NotFoundException {
    Guest authenticatedGuest = authenticationHelper.getAuthenticatedGuest();
    Event foundEvent = eventCrudService.findByIdAndGuest(id, authenticatedGuest);
    return eventMapper.toDto(foundEvent);
  }

  @Override
  public List<EventDto> getAllByFilterAndGuest(
      String search, String sortBy, String sortDirection, Integer page, Integer size)
      throws InvalidJwtTokenException, NotFoundException {
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
    Guest authenticatedGuest = authenticationHelper.getAuthenticatedGuest();
    return eventCrudService.findAllByFilterAndGuest(search, pageable, authenticatedGuest).stream()
        .map(eventMapper::toDto)
        .toList();
  }

  @Override
  public PhotoDto uploadPhoto(MultipartFile file) throws Exception {
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new BadRequestException("Invalid file format. Only image files are allowed.");
    }

    Event foundEvent = authenticationHelper.getActiveEvent();
    String normalizedEventName = normalizeFolderName(foundEvent.getName());
    String photoUrl = storageService.uploadFile(file, normalizedEventName);

    Photo photo = new Photo();
    photo.setPhotoUrl(photoUrl);
    photo.setUploadDate(extractPhotoTakenDate(file));
    photo.setEvent(foundEvent);
    photo.setContentType(contentType);

    Photo savedPhoto = photoCrudService.savePhoto(photo);
    return photoMapper.toDto(savedPhoto);
  }

  @Override
  public List<PhotoRecognitionDto> getMatchedPhotos(MultipartFile file)
      throws InvalidJwtTokenException, NotFoundException, IOException {
    Event foundEvent = authenticationHelper.getActiveEvent();
    String normalizedEventName = normalizeFolderName(foundEvent.getName());
    MatchedPhotosResponse matchedPathsResponse =
        photoMatchingService.getMatchedPhotos(normalizedEventName, file);
    List<MatchedPhotoDto> matchedPhotos = matchedPathsResponse.getMatchedPhotos();

    Map<String, MatchedPhotoDto> matchedMap =
        matchedPhotos.stream()
            .collect(Collectors.toMap(MatchedPhotoDto::getFilePath, Function.identity()));

    return foundEvent.getPhotos().stream()
        .filter(photo -> matchedMap.containsKey(photo.getPhotoUrl()))
        .map(
            photo -> {
              PhotoRecognitionDto dto = new PhotoRecognitionDto();
              dto.setId(photo.getId());
              dto.setPhotoUrl(photo.getPhotoUrl());
              dto.setContentType(photo.getContentType());
              dto.setUploadDate(photo.getUploadDate());
              dto.setFaceBoundingBox(matchedMap.get(photo.getPhotoUrl()).getFaceBoundingBox());
              return dto;
            })
        .toList();
  }

  @Override
  public List<PhotoDto> getPhotosByEvent() throws InvalidJwtTokenException, NotFoundException {
    Event foundEvent = authenticationHelper.getActiveEvent();
    return foundEvent.getPhotos().stream().map(photoMapper::toDto).toList();
  }

  @Override
  public GetSinglePhotoDto getPhotoByUrl(String url)
      throws InvalidJwtTokenException, NotFoundException {
    Event foundEvent = authenticationHelper.getActiveEvent();
    return foundEvent.getPhotos().stream()
        .filter(photo -> photo.getPhotoUrl().equals(url))
        .map(
            photo -> {
              GetSinglePhotoDto singlePhotoDto = new GetSinglePhotoDto();
              Path photoPath = Paths.get(photo.getPhotoUrl());
              String fileName = photoPath.getFileName().toString();
              singlePhotoDto.setFileName(fileName);
              singlePhotoDto.setContentType(photo.getContentType());
              singlePhotoDto.setImageBytes(readFileBytes(photoPath));
              return singlePhotoDto;
            })
        .findFirst()
        .orElseThrow(() -> new NotFoundException("Photo not found"));
  }

  @Override
  public void deletePhoto(Long photoId) throws NotFoundException, InvalidJwtTokenException {
    Event foundEvent = authenticationHelper.getActiveEvent();
    Photo photoToDelete =
        foundEvent.getPhotos().stream()
            .filter(photo -> photo.getId().equals(photoId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Photo not found in the current event."));
    Path photoPath = Paths.get(photoToDelete.getPhotoUrl());
    try {
      Files.deleteIfExists(photoPath);
    } catch (IOException e) {
      throw new RuntimeException("Error deleting photo file", e);
    }
    photoCrudService.delete(photoId);
  }

  @Override
  public DownloadPhotosDto downloadEventPhotos()
      throws InvalidJwtTokenException, NotFoundException {
    Event foundEvent = authenticationHelper.getActiveEvent();
    List<Photo> photos = foundEvent.getPhotos();

    byte[] zipBytes = getRequestsPhotos(photos);

    DownloadPhotosDto dto = new DownloadPhotosDto();
    dto.setResource(new InputStreamResource(new ByteArrayInputStream(zipBytes)));
    dto.setContentLength(zipBytes.length);
    String normalizedEventName = foundEvent.getName().trim().toLowerCase().replaceAll("\\s+", "_");
    dto.setEventName(normalizedEventName);
    return dto;
  }

  @Override
  public EventDto getActiveEvent() throws InvalidJwtTokenException, NotFoundException {
    Event activeEvent = authenticationHelper.getActiveEvent();
    return eventMapper.toDto(activeEvent);
  }

  @Override
  public DownloadPhotosDto downloadEventMatchPhotos(List<Long> photoIds)
      throws InvalidJwtTokenException, NotFoundException {
    Event foundEvent = authenticationHelper.getActiveEvent();
    List<Photo> photos =
        foundEvent.getPhotos().stream().filter(photo -> photoIds.contains(photo.getId())).toList();

    byte[] zipBytes = getRequestsPhotos(photos);

    DownloadPhotosDto dto = new DownloadPhotosDto();
    dto.setResource(new InputStreamResource(new ByteArrayInputStream(zipBytes)));
    dto.setContentLength(zipBytes.length);
    String normalizedEventName = foundEvent.getName().trim().toLowerCase().replaceAll("\\s+", "_");
    dto.setEventName(normalizedEventName);
    return dto;
  }

  @Override
  public List<PhotoDto> getHostEventPhotosByEventId(Long id)
      throws InvalidJwtTokenException, NotFoundException {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);
    return foundEvent.getPhotos().stream().map(photoMapper::toDto).toList();
  }

  @Override
  public List<PhotoDto> getGuestEventPhotosByEventId(Long id)
      throws InvalidJwtTokenException, NotFoundException {
    Guest authenticatedGuest = authenticationHelper.getAuthenticatedGuest();
    Event foundEvent = eventCrudService.findByIdAndGuest(id, authenticatedGuest);
    return foundEvent.getPhotos().stream().map(photoMapper::toDto).toList();
  }

  @Override
  public GetSinglePhotoDto getGuestEventPhotoByUrl(String url)
      throws InvalidJwtTokenException, NotFoundException {
    Guest authenticatedGuest = authenticationHelper.getAuthenticatedGuest();
    Photo foundPhoto = photoCrudService.findByUrl(url);
    GetSinglePhotoDto singlePhotoDto = new GetSinglePhotoDto();
    Path photoPath = Paths.get(foundPhoto.getPhotoUrl());
    String fileName = photoPath.getFileName().toString();
    singlePhotoDto.setFileName(fileName);
    singlePhotoDto.setContentType(foundPhoto.getContentType());
    singlePhotoDto.setImageBytes(readFileBytes(photoPath));
    return singlePhotoDto;
  }

  @Override
  public GetSinglePhotoDto getHostEventPhotoByUrl(String url)
      throws InvalidJwtTokenException, NotFoundException {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Photo foundPhoto = photoCrudService.findByUrl(url);
    GetSinglePhotoDto singlePhotoDto = new GetSinglePhotoDto();
    Path photoPath = Paths.get(foundPhoto.getPhotoUrl());
    String fileName = photoPath.getFileName().toString();
    singlePhotoDto.setFileName(fileName);
    singlePhotoDto.setContentType(foundPhoto.getContentType());
    singlePhotoDto.setImageBytes(readFileBytes(photoPath));
    return singlePhotoDto;
  }

  private byte[] getRequestsPhotos(List<Photo> photos) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ZipOutputStream zos = new ZipOutputStream(baos)) {
      for (Photo photo : photos) {
        Path photoPath = Paths.get(photo.getPhotoUrl());
        if (Files.exists(photoPath)) {
          ZipEntry entry = new ZipEntry(photoPath.getFileName().toString());
          zos.putNextEntry(entry);
          zos.write(readFileBytes(photoPath));
          zos.closeEntry();
        }
      }
      zos.finish();
    } catch (IOException e) {
      throw new RuntimeException("Error creating zip file", e);
    }
    return baos.toByteArray();
  }

  private byte[] readFileBytes(Path path) {
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new RuntimeException("Error reading image file", e);
    }
  }
}

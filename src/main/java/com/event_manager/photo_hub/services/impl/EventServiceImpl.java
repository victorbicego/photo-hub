package com.event_manager.photo_hub.services.impl;

import static com.event_manager.photo_hub.services.utils.DateUtil.validateDateRange;
import static com.event_manager.photo_hub.services.utils.MetadataUtil.extractPhotoTakenDate;
import static com.event_manager.photo_hub.services.utils.QrCodeGeneratorUtil.generateQrCode;
import static com.event_manager.photo_hub.services.utils.StringUtil.normalizeFolderName;

import com.drew.imaging.ImageProcessingException;
import com.event_manager.photo_hub.exceptions.BadRequestException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.dtos.*;
import com.event_manager.photo_hub.models.entities.Event;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.models.entities.Photo;
import com.event_manager.photo_hub.models.mappers.EventMapper;
import com.event_manager.photo_hub.models.mappers.HostMapper;
import com.event_manager.photo_hub.models.mappers.PhotoMapper;
import com.event_manager.photo_hub.services.EventService;
import com.event_manager.photo_hub.services.PhotoMatchingService;
import com.event_manager.photo_hub.services.StorageService;
import com.event_manager.photo_hub.services.helpers.AuthenticationHelper;
import com.event_manager.photo_hub.services_crud.EventCrudService;
import com.event_manager.photo_hub.services_crud.HostCrudService;
import com.event_manager.photo_hub.services_crud.PhotoCrudService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
  private final HostCrudService hostCrudService;
  private final HostMapper hostMapper;

  @Override
  public List<EventDto> getAllByFilter(
      String search, String sortBy, String sortDirection, Integer page, Integer size) {
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    return eventCrudService.findAllByFilterAndHost(search, pageable, authenticatedHost).stream()
        .map(eventMapper::toDto)
        .toList();
  }

  @Override
  public EventDto getById(Long id) {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);
    return eventMapper.toDto(foundEvent);
  }

  @Override
  public EventDto create(CreateEventDto createEventDto) {
    validateDateRange(createEventDto.getStartDate(), createEventDto.getEndDate());
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event event = eventMapper.toEntity(createEventDto);
    event.setHost(authenticatedHost);
    String uniqueData = event.getName().trim() + "_" + System.currentTimeMillis();
    event.setQrCodeData(uniqueData);
    event.setQrCode(generateQrCode(uniqueData));
    Event savedEvent = eventCrudService.save(event);
    return eventMapper.toDto(savedEvent);
  }

  @Override
  public EventDto update(Long id, UpdateEventDto updateEventDto) {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);

    //if (foundEvent
    //    .getStartDate()
    //    .isBefore(ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime())) {
    //  throw new BadRequestException("Event has already started. Cannot update.");
    //}

    foundEvent.setName(updateEventDto.getName());
    foundEvent.setStartDate(updateEventDto.getStartDate());
    foundEvent.setEndDate(updateEventDto.getEndDate());
    Event savedEvent = eventCrudService.save(foundEvent);
    return eventMapper.toDto(savedEvent);
  }

  @Override
  public void delete(Long id) {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);

    //if (foundEvent
    //    .getStartDate()
    //    .isBefore(ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime())) {
    //  throw new BadRequestException("Event has already started. Cannot delete.");
    //}

    eventCrudService.delete(foundEvent.getId());
  }

  @Override
  public EventDto getActiveEvent() {
    Event activeEvent = authenticationHelper.getActiveEvent();
    return eventMapper.toDto(activeEvent);
  }

  @Override
  public List<PhotoDto> getPhotosByActiveEvent() {
    Event foundEvent = authenticationHelper.getActiveEvent();
    return foundEvent.getPhotos().stream().map(photoMapper::toDto).toList();
  }

  @Override
  public List<PhotoDto> getPhotosByEventId(Long id) {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);
    return foundEvent.getPhotos().stream().map(photoMapper::toDto).toList();
  }

  @Override
  public PhotoDto uploadPhoto(MultipartFile file, String clientIp)
      throws IOException, ImageProcessingException {
    Event foundEvent = authenticationHelper.getActiveEvent();

    if (foundEvent.getBlockedUsers().contains(clientIp)) {
      throw new BadRequestException("User is blocked. Cannot upload.");
    }

    LocalDateTime photoTakenDate = extractPhotoTakenDate(file);

    long timeBeforeToUpload = 30L;
    validatePhoto(foundEvent.getStartDate(), timeBeforeToUpload, photoTakenDate);

    Photo photo = createPhoto(foundEvent, file, photoTakenDate, clientIp);
    Photo savedPhoto = photoCrudService.savePhoto(photo);
    return photoMapper.toDto(savedPhoto);
  }

  @Override
  public PhotoDto uploadPhoto(Long id, MultipartFile file)
      throws IOException, ImageProcessingException {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);

    if (foundEvent.getBlockedUsers().contains(authenticatedHost.getUsername())) {
      throw new BadRequestException("User is blocked. Cannot upload.");
    }

    LocalDateTime photoTakenDate = extractPhotoTakenDate(file);

    long timeBeforeToUpload = 60L;
    validatePhoto(foundEvent.getStartDate(), timeBeforeToUpload, photoTakenDate);

    Photo photo = createPhoto(foundEvent, file, photoTakenDate, authenticatedHost.getUsername());
    Photo savedPhoto = photoCrudService.savePhoto(photo);
    return photoMapper.toDto(savedPhoto);
  }

  private void validatePhoto(
      LocalDateTime startDate, Long timeBeforeToUpload, LocalDateTime photoTakenDate) {
    // if (startDate.isAfter(ZonedDateTime.now(ZoneId.systemDefault())
    //        .toLocalDateTime().minusMinutes(timeBeforeToUpload))) {
    //  throw new BadRequestException("Event has not started yet. Cannot upload photos.");
    // }
    //
    // if (photoTakenDate.isAfter(ZonedDateTime.now(ZoneId.systemDefault())
    //        .toLocalDateTime().minusMinutes(timeBeforeToUpload))) {
    //  throw new BadRequestException("Photo was taken before the event started.");
    // }
  }

  @Override
  public List<PhotoRecognitionDto> getMatchedPhotos(MultipartFile file) {
    Event foundEvent = authenticationHelper.getActiveEvent();
    String normalizedEventName = normalizeFolderName(foundEvent.getName());
    MatchedPhotosResponse matchedResponse =
        photoMatchingService.getMatchedPhotos(normalizedEventName, file);
    List<MatchedPhotoDto> matchedPhotos = matchedResponse.getMatchedPhotos();
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
  public GetSinglePhotoDto getPhotoByUrl(String url) {
    Event foundEvent = authenticationHelper.getActiveEvent();
    return foundEvent.getPhotos().stream()
        .filter(photo -> photo.getPhotoUrl().equals(url))
        .map(
            photo -> {
              GetSinglePhotoDto dto = new GetSinglePhotoDto();
              Path photoPath = Paths.get(photo.getPhotoUrl());
              dto.setFileName(photoPath.getFileName().toString());
              dto.setContentType(photo.getContentType());
              dto.setImageBytes(readFileBytes(photoPath));
              return dto;
            })
        .findFirst()
        .orElseThrow(() -> new NotFoundException("Photo not found"));
  }

  @Override
  public GetSinglePhotoDto getHostEventPhotoByUrl(String url) {
    authenticationHelper.getAuthenticatedHost();
    Photo foundPhoto = photoCrudService.findByUrl(url);
    GetSinglePhotoDto dto = new GetSinglePhotoDto();
    Path photoPath = Paths.get(foundPhoto.getPhotoUrl());
    dto.setFileName(photoPath.getFileName().toString());
    dto.setContentType(foundPhoto.getContentType());
    dto.setImageBytes(readFileBytes(photoPath));
    return dto;
  }

  @Override
  public DownloadPhotosDto downloadPhotos(Long id, PhotoListDto photoListDto) {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);
    List<Photo> photosToDownload =
        foundEvent.getPhotos().stream()
            .filter(photo -> photoListDto.getIdList().contains(photo.getId()))
            .toList();
    if (photosToDownload.isEmpty()) {
      photosToDownload = foundEvent.getPhotos();
    }
    return buildDownloadPhotosDto(foundEvent, photosToDownload);
  }

  @Override
  public DownloadPhotosDto downloadPhotosByActiveEvent(PhotoListDto photoListDto) {
    Event activeEvent = authenticationHelper.getActiveEvent();
    List<Photo> photosToDownload =
        activeEvent.getPhotos().stream()
            .filter(photo -> photoListDto.getIdList().contains(photo.getId()))
            .toList();
    if (photosToDownload.isEmpty()) {
      photosToDownload = activeEvent.getPhotos();
    }
    return buildDownloadPhotosDto(activeEvent, photosToDownload);
  }

  @Override
  public void deletePhotos(Long id, PhotoListDto photoListDto) {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);
    List<Photo> photosToDelete =
        foundEvent.getPhotos().stream()
            .filter(photo -> photoListDto.getIdList().contains(photo.getId()))
            .toList();
    photosToDelete.forEach(photo -> deleteSinglePhoto(photo, foundEvent));
  }

  @Override
  public void blockIp(Long id, PhotoListDto photoListDto) {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();
    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);

    List<String> usersToBlock =
        foundEvent.getPhotos().stream()
            .filter(photo -> photoListDto.getIdList().contains(photo.getId()))
            .map(Photo::getUploadedBy)
            .toList();

    if (usersToBlock.contains(authenticatedHost.getUsername())) {
      throw new BadRequestException("You cannot block yourself.");
    }

    for (String user : usersToBlock) {
      foundEvent.getBlockedUsers().add(user);
    }

    eventCrudService.save(foundEvent);
  }

  @Override
  public EventDto addCoHosts(Long id, CoHostListDto coHostListDto) {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();

    if (coHostListDto.getCoHostEmails().contains(authenticatedHost.getUsername())) {
      throw new BadRequestException("You cannot add yourself as a co-host.");
    }

    Event foundEvent = eventCrudService.findByIdAndHost(id, authenticatedHost);

    List<Host> coHosts =
        coHostListDto.getCoHostEmails().stream().map(hostCrudService::findByUsername).toList();

    foundEvent.getCoHosts().clear();
    foundEvent.getCoHosts().addAll(coHosts);

    Event savedEvent = eventCrudService.save(foundEvent);

    return eventMapper.toDto(savedEvent);
  }

  @Override
  public HostDto getHostByUsername(String username) {
    Host authenticatedHost = authenticationHelper.getAuthenticatedHost();

    if (username.equals(authenticatedHost.getUsername())) {
      throw new BadRequestException("You cannot get yourself as coHost.");
    }

    Host foundHost = hostCrudService.findByUsername(username);
    return hostMapper.toDto(foundHost);
  }

  private Photo createPhoto(
      Event foundEvent, MultipartFile file, LocalDateTime photoTakenDate, String photoOwner)
      throws IOException {
    String normalizedEventName = normalizeFolderName(foundEvent.getName());
    String photoUrl = storageService.uploadFile(file, normalizedEventName);
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/"))
      throw new BadRequestException("Invalid file format. Only image files are allowed.");
    Photo photo = new Photo();
    photo.setPhotoUrl(photoUrl);
    photo.setUploadDate(photoTakenDate);
    photo.setEvent(foundEvent);
    photo.setContentType(contentType);
    photo.setDescription(foundEvent.getName() + " - " + photo.getUploadDate());
    photo.setUploadedBy(photoOwner);
    return photo;
  }

  private void deleteSinglePhoto(Photo photo, Event event) {
    Path photoPath = Paths.get(photo.getPhotoUrl());
    try {
      Files.deleteIfExists(photoPath);
    } catch (IOException e) {
      throw new RuntimeException("Error deleting photo file", e);
    }
    event.getPhotos().remove(photo);
    eventCrudService.save(event);
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

  private DownloadPhotosDto buildDownloadPhotosDto(Event event, List<Photo> photos) {
    byte[] zipBytes = getRequestsPhotos(photos);
    DownloadPhotosDto dto = new DownloadPhotosDto();
    dto.setResource(new InputStreamResource(new ByteArrayInputStream(zipBytes)));
    dto.setContentLength(zipBytes.length);
    String normalizedEventName = event.getName().trim().toLowerCase().replaceAll("\\s+", "_");
    dto.setEventName(normalizedEventName);
    return dto;
  }
}

package com.event_manager.photo_hub.controllers.privates;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.*;
import com.event_manager.photo_hub.services.EventService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/host/event")
@RequiredArgsConstructor
@Validated
public class HostEventController {

  private final EventService eventService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<EventDto>>> getAllEventsForHost(
      @RequestParam(defaultValue = "", required = false) String search,
      @RequestParam(defaultValue = "name", required = false) String sortBy,
      @RequestParam(defaultValue = "asc", required = false) String sortDirection,
      @RequestParam(defaultValue = "0", required = false) @Min(0) Integer page,
      @RequestParam(defaultValue = "10", required = false) @Min(0) Integer size)
      throws InvalidJwtTokenException, NotFoundException {
    List<EventDto> eventDtoList =
        eventService.getAllByFilterAndHost(search, sortBy, sortDirection, page, size);
    return buildResponse(HttpStatus.OK, eventDtoList, "Events retrieved successfully.");
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<EventDto>> getEventByIdForHost(@PathVariable @Positive Long id)
      throws InvalidJwtTokenException, NotFoundException {
    EventDto eventDto = eventService.getByIdAndHost(id);
    return buildResponse(HttpStatus.OK, eventDto, "Event retrieved successfully.");
  }

  @PostMapping
  public ResponseEntity<ApiResponse<EventDto>> createEvent(@RequestBody CreateEventDto event)
      throws InvalidJwtTokenException, NotFoundException {
    EventDto createdEvent = eventService.createEvent(event);
    return buildResponse(HttpStatus.OK, createdEvent, "Event created.");
  }

  @DeleteMapping("/photo/{photoId}")
  public ResponseEntity<ApiResponse<Void>> deletePhoto(@PathVariable Long photoId)
      throws NotFoundException, InvalidJwtTokenException {
    eventService.deletePhoto(photoId);
    return buildResponse(HttpStatus.OK, null, "Photo deleted successfully.");
  }

  @DeleteMapping("/{id}/photos")
  public ResponseEntity<ApiResponse<Void>> deletePhotos(
      @PathVariable @Positive Long id, @RequestBody PhotoListDto photoListDto)
      throws NotFoundException, InvalidJwtTokenException {

    eventService.deleteSelectedPhotos(id, photoListDto);

    return buildResponse(HttpStatus.OK, null, "Photos deleted successfully.");
  }

  @GetMapping("/{id}/photos")
  public ResponseEntity<ApiResponse<List<PhotoDto>>> getEventPhotosByEventId(
      @PathVariable @Positive Long id) throws InvalidJwtTokenException, NotFoundException {
    List<PhotoDto> photos = eventService.getHostEventPhotosByEventId(id);
    return buildResponse(HttpStatus.OK, photos, "Photos found.");
  }

  @PostMapping("/{id}/photo")
  public ResponseEntity<ApiResponse<PhotoDto>> uploadPhoto(
      @PathVariable @Positive Long id, @RequestParam("file") MultipartFile file) throws Exception {
    PhotoDto photo = eventService.uploadHostPhoto(id, file);
    return buildResponse(HttpStatus.OK, photo, "Photo saved.");
  }

  @GetMapping("/photo")
  public ResponseEntity<ByteArrayResource> getHostEventPhotoByUrl(@RequestParam("url") String url)
      throws InvalidJwtTokenException, NotFoundException {
    GetSinglePhotoDto getSinglePhoto = eventService.getHostEventPhotoByUrl(url);
    ByteArrayResource resource = new ByteArrayResource(getSinglePhoto.getImageBytes());
    MediaType mediaType = MediaType.parseMediaType(getSinglePhoto.getContentType());
    String contentDisposition = "attachment; filename=\"" + getSinglePhoto.getFileName() + "\"";
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .contentLength(getSinglePhoto.getImageBytes().length)
        .contentType(mediaType)
        .body(resource);
  }

  @PostMapping("/{id}/photos/download")
  public ResponseEntity<InputStreamResource> downloadEventPhotos(
      @PathVariable @Positive Long id, @RequestBody PhotoListDto photoListDto)
      throws InvalidJwtTokenException, NotFoundException {
    DownloadPhotosDto downloadPhotos = eventService.downloadSelectedPhotos(id, photoListDto);
    String fileName = downloadPhotos.getEventName() + "_photos.zip";
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
    headers.add("Access-Control-Expose-Headers", "Content-Disposition");

    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(downloadPhotos.getContentLength())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(downloadPhotos.getResource());
  }
}

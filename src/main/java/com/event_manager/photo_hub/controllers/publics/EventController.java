package com.event_manager.photo_hub.controllers.publics;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.*;
import com.event_manager.photo_hub.services.EventService;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
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
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
@Validated
public class EventController {

  private final EventService eventService;

  @PostMapping("/photo")
  public ResponseEntity<ApiResponse<PhotoDto>> uploadPhoto(@RequestParam("file") MultipartFile file)
      throws Exception {
    PhotoDto photo = eventService.uploadPhoto(file);
    return buildResponse(HttpStatus.OK, photo, "Photo saved.");
  }

  @PostMapping("/photo/match")
  public ResponseEntity<ApiResponse<List<PhotoRecognitionDto>>> getMatchedPhotos(
      @RequestParam("file") MultipartFile file)
      throws InvalidJwtTokenException, NotFoundException, IOException {
    List<PhotoRecognitionDto> photos = eventService.getMatchedPhotos(file);
    return buildResponse(HttpStatus.OK, photos, "Photos found.");
  }

  @GetMapping("/photo/all")
  public ResponseEntity<ApiResponse<List<PhotoDto>>> getEventPhotos()
      throws InvalidJwtTokenException, NotFoundException {
    List<PhotoDto> photos = eventService.getPhotosByEvent();
    return buildResponse(HttpStatus.OK, photos, "Photos found.");
  }

  @GetMapping("/photo")
  public ResponseEntity<ByteArrayResource> getPhotoByUrl(@RequestParam("url") String url)
      throws InvalidJwtTokenException, NotFoundException {
    GetSinglePhotoDto getSinglePhoto = eventService.getPhotoByUrl(url);
    ByteArrayResource resource = new ByteArrayResource(getSinglePhoto.getImageBytes());
    MediaType mediaType = MediaType.parseMediaType(getSinglePhoto.getContentType());
    String contentDisposition = "attachment; filename=\"" + getSinglePhoto.getFileName() + "\"";
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .contentLength(getSinglePhoto.getImageBytes().length)
        .contentType(mediaType)
        .body(resource);
  }

  @GetMapping("/photo/download")
  public ResponseEntity<InputStreamResource> downloadEventPhotos()
      throws InvalidJwtTokenException, NotFoundException {
    DownloadPhotosDto downloadPhotos = eventService.downloadEventPhotos();
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

  @GetMapping("/photo/download/match")
  public ResponseEntity<InputStreamResource> downloadEventMatchPhotos(
      @RequestParam @NotNull List<Long> photoIds)
      throws InvalidJwtTokenException, NotFoundException {
    DownloadPhotosDto downloadPhotos = eventService.downloadEventMatchPhotos(photoIds);
    String fileName = downloadPhotos.getEventName() + "_matched_photos.zip";
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
    headers.add("Access-Control-Expose-Headers", "Content-Disposition");

    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(downloadPhotos.getContentLength())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(downloadPhotos.getResource());
  }

  @GetMapping
  public ResponseEntity<ApiResponse<EventDto>> getActiveEvent()
      throws InvalidJwtTokenException, NotFoundException {
    EventDto eventDto = eventService.getActiveEvent();
    return buildResponse(HttpStatus.OK, eventDto, "Event retrieved successfully.");
  }
}

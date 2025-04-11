package com.event_manager.photo_hub.controllers.privates;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;
import static com.event_manager.photo_hub.controllers.ControllerUtilService.validateDateRange;
import static com.event_manager.photo_hub.controllers.ControllerUtilService.validateFromDateInFuture;
import static com.event_manager.photo_hub.controllers.ControllerUtilService.validateMax7Days;

import com.drew.imaging.ImageProcessingException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.*;
import com.event_manager.photo_hub.services.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/host/event")
@RequiredArgsConstructor
@Validated
public class HostEventController {

  private final EventService eventService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<EventDto>>> getAllEvents(
      @RequestParam(defaultValue = "", required = false) String search,
      @RequestParam(defaultValue = "name", required = false) String sortBy,
      @RequestParam(defaultValue = "asc", required = false) String sortDirection,
      @RequestParam(defaultValue = "0", required = false) @Min(0) Integer page,
      @RequestParam(defaultValue = "10", required = false) @Min(0) Integer size) {
    List<EventDto> eventDtoList =
        eventService.getAllByFilter(search, sortBy, sortDirection, page, size);
    return buildResponse(HttpStatus.OK, eventDtoList, "Events retrieved successfully.");
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<EventDto>> getEventById(@PathVariable @Positive Long id) {
    EventDto eventDto = eventService.getById(id);
    return buildResponse(HttpStatus.OK, eventDto, "Event retrieved successfully.");
  }

  @PostMapping
  public ResponseEntity<ApiResponse<EventDto>> createEvent(
      @RequestBody @Valid CreateEventDto createEventDto) {
    EventDto eventDto = eventService.create(createEventDto);
    validateDateRange(createEventDto.getStartDate(), createEventDto.getEndDate());
    validateFromDateInFuture(createEventDto.getStartDate());
    validateMax7Days(createEventDto.getStartDate(), createEventDto.getEndDate());
    return buildResponse(HttpStatus.CREATED, eventDto, "Event created successfully.");
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<EventDto>> updateEvent(
      @PathVariable @Positive Long id, @RequestBody @Valid UpdateEventDto updateEventDto) {
    validateDateRange(updateEventDto.getStartDate(), updateEventDto.getEndDate());
    validateFromDateInFuture(updateEventDto.getStartDate());
    validateMax7Days(updateEventDto.getStartDate(), updateEventDto.getEndDate());
    EventDto eventDto = eventService.update(id, updateEventDto);
    return buildResponse(HttpStatus.OK, eventDto, "Event updated successfully.");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteEventById(@PathVariable @Positive Long id) {
    eventService.delete(id);
    return buildResponse(HttpStatus.OK, null, "Event deleted successfully.");
  }

  @GetMapping("/{id}/photos")
  public ResponseEntity<ApiResponse<List<PhotoDto>>> getPhotos(@PathVariable @Positive Long id) {
    List<PhotoDto> photoDtoList = eventService.getPhotosByEventId(id);
    return buildResponse(HttpStatus.OK, photoDtoList, "Photos retrieved successfully.");
  }

  @GetMapping("/photos/single")
  public ResponseEntity<ByteArrayResource> getPhotoByUrl(@RequestParam("url") String url) {

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
  public ResponseEntity<InputStreamResource> downloadPhotos(
      @PathVariable @Positive Long id, @RequestBody @Valid PhotoListDto photoListDto) {

    DownloadPhotosDto downloadPhotos = eventService.downloadPhotos(id, photoListDto);
    HttpHeaders headers = new HttpHeaders();
    headers.add(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + downloadPhotos.getFileName() + "\"");
    headers.add("Access-Control-Expose-Headers", "Content-Disposition");

    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(downloadPhotos.getContentLength())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(downloadPhotos.getResource());
  }

  @PostMapping("/{id}/photos")
  public ResponseEntity<ApiResponse<PhotoDto>> uploadPhoto(
      @PathVariable @Positive Long id, @RequestParam("file") MultipartFile file)
      throws IOException, ImageProcessingException {
    PhotoDto photoDto = eventService.uploadPhoto(id, file);
    return buildResponse(HttpStatus.OK, photoDto, "Photo saved.");
  }

  @DeleteMapping("/{id}/photos")
  public ResponseEntity<ApiResponse<Void>> deletePhotos(
      @PathVariable @Positive Long id, @RequestBody @Valid PhotoListDto photoListDto) {
    eventService.deletePhotos(id, photoListDto);
    return buildResponse(HttpStatus.OK, null, "Photos deleted successfully.");
  }

  @PostMapping("/{id}/block")
  public ResponseEntity<ApiResponse<Void>> blockUser(
      @PathVariable @Positive Long id, @RequestBody @Valid PhotoListDto photoListDto) {
    eventService.blockIp(id, photoListDto);
    return buildResponse(HttpStatus.OK, null, "IP blocked successfully.");
  }

  @PostMapping("/{id}/cohosts")
  public ResponseEntity<ApiResponse<EventDto>> addCoHosts(
      @PathVariable @Positive Long id, @RequestBody @Valid CoHostListDto coHostListDto) {
    EventDto eventDto = eventService.addCoHosts(id, coHostListDto);
    return buildResponse(HttpStatus.OK, eventDto, "CoHosts added successfully.");
  }

  @GetMapping("/cohosts")
  public ResponseEntity<ApiResponse<HostDto>> getCoHosts(@RequestParam("username") String username) {
    HostDto hostDto = eventService.getHostByUsername(username);
    return buildResponse(HttpStatus.OK, hostDto, "CoHosts retrieved successfully.");
  }
}

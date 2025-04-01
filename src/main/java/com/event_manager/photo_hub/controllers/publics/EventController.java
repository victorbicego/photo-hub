package com.event_manager.photo_hub.controllers.publics;

import static com.event_manager.photo_hub.controllers.ControllerUtilService.buildResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.ApiResponse;
import com.event_manager.photo_hub.models.dtos.DownloadPhotosDto;
import com.event_manager.photo_hub.models.dtos.EventDto;
import com.event_manager.photo_hub.models.dtos.GetSinglePhotoDto;
import com.event_manager.photo_hub.models.dtos.PhotoDto;
import com.event_manager.photo_hub.models.dtos.PhotoListDto;
import com.event_manager.photo_hub.models.dtos.PhotoRecognitionDto;
import com.event_manager.photo_hub.services.EventService;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<EventDto>> getActiveEvent()
            throws InvalidJwtTokenException, NotFoundException {
        EventDto eventDto = eventService.getActiveEvent();
        return buildResponse(HttpStatus.OK, eventDto, "Event retrieved successfully.");
    }

    @GetMapping("/photos")
    public ResponseEntity<ApiResponse<List<PhotoDto>>> getEventPhotos() {
        List<PhotoDto> photos = eventService.getPhotosByEvent();
        return buildResponse(HttpStatus.OK, photos, "Photos found.");
    }

    @PostMapping("/photos")
    public ResponseEntity<ApiResponse<PhotoDto>> uploadPhoto(
            @RequestParam("file") @NotNull MultipartFile file) {
        PhotoDto photo = eventService.uploadPhoto(file);
        return buildResponse(HttpStatus.OK, photo, "Photo saved.");
    }

    @PostMapping("/photos/match")
    public ResponseEntity<ApiResponse<List<PhotoRecognitionDto>>> getMatchedPhotos(
            @RequestParam("file") @NotNull MultipartFile file) {
        List<PhotoRecognitionDto> photos = eventService.getMatchedPhotos(file);
        return buildResponse(HttpStatus.OK, photos, "Photos found.");
    }

    @GetMapping("/photos/single")
    public ResponseEntity<ByteArrayResource> getPhotoByUrl(@RequestParam("url") String url) {

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

    @PostMapping("/photos/download")
    public ResponseEntity<InputStreamResource> downloadEventPhotos(
            @RequestBody @Valid PhotoListDto photoListDto) {

        DownloadPhotosDto downloadPhotos =
                eventService.downloadSelectedPhotosFromActiveEvent(photoListDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadPhotos.getFileName() + "\"");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(downloadPhotos.getContentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(downloadPhotos.getResource());
    }
}

package com.event_manager.photo_hub.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.event_manager.photo_hub.models.dtos.MatchedPhotosResponse;

public interface PhotoMatchingService {

    MatchedPhotosResponse getMatchedPhotos(String event, MultipartFile file) throws IOException;
}

package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.models.dtos.MatchedPhotosResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoMatchingService {

  MatchedPhotosResponse getMatchedPhotos(String event, MultipartFile file);
}

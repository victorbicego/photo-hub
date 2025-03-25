package com.event_manager.photo_hub.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  String uploadFile(MultipartFile file, String eventName) throws Exception;
}

package com.event_manager.photo_hub.services.impl;

import static com.event_manager.photo_hub.services.utils.StringUtil.normalizeFolderName;

import com.event_manager.photo_hub.services.StorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

  private final Path rootLocation = Paths.get("uploads");

  @Override
  public String uploadFile(MultipartFile file, String eventName) throws IOException {
    String folderName = normalizeFolderName(eventName);
    Path eventDirectory = rootLocation.resolve(folderName);
    if (!Files.exists(eventDirectory)) {
      Files.createDirectories(eventDirectory);
    }
    String filename = buildFileName(file.getOriginalFilename(), eventName);
    Path destinationFile = eventDirectory.resolve(filename).normalize().toAbsolutePath();
    if (!destinationFile.getParent().equals(eventDirectory.toAbsolutePath())) {
      throw new RuntimeException("Folder not allowed.");
    }
    Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
    return destinationFile.toString();
  }

  private String buildFileName(String originalFilename, String eventName) {
    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
      extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    return eventName + "_" + UUID.randomUUID() + extension;
  }
}

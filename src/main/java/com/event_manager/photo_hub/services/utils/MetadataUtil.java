package com.event_manager.photo_hub.services.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.web.multipart.MultipartFile;

public class MetadataUtil {

  public static LocalDateTime extractPhotoTakenDate(MultipartFile file)
      throws IOException, ImageProcessingException {
    Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
    ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
    if (directory != null) {
      Date dateOriginal = directory.getDateOriginal();
      if (dateOriginal != null) {
        return LocalDateTime.ofInstant(dateOriginal.toInstant(), ZoneId.systemDefault());
      }
    }
    return LocalDateTime.now();
  }
}

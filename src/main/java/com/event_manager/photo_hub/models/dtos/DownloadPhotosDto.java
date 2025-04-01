package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadPhotosDto {

  @NotNull private InputStreamResource resource;

  @NotNull private String eventName;

  @NotNull private long contentLength;

  @NotNull private String fileName;
}

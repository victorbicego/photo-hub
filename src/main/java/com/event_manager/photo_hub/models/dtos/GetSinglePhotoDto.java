package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSinglePhotoDto {

  @NotNull private byte[] imageBytes;

  @NotNull private String fileName;

  @NotNull private String contentType;
}

package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchedPhotoDto {

  @NotNull private String filePath;

  @NotNull private FaceBoundingBoxDto faceBoundingBox;
}

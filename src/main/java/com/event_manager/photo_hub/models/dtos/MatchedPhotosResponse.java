package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchedPhotosResponse {

  @NotNull private List<MatchedPhotoDto> matchedPhotos;
}

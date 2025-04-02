package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDto {

  @NotNull private Long id;

  @NotNull private String photoUrl;

  @NotNull private LocalDateTime uploadDate;

  @NotNull private String contentType;

  @NotNull
  private String description;

  @NotNull
  private String uploadedBy;
}

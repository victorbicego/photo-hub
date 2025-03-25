package com.event_manager.photo_hub.models.dtos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDto {

  @NotNull private Long id;

  @NotNull private String photoUrl;

  @NotNull private LocalDateTime uploadDate;

  @NotNull private String contentType;
}

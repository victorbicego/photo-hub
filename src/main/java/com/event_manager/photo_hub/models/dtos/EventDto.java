package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

  @NotNull private Long id;

  @NotNull
  @Size(min = 1, max = 100)
  private String name;

  @NotNull private LocalDateTime startDate;

  @NotNull private LocalDateTime endDate;

  @NotNull private String qrCode;

  @NotNull
  private String qrCodeData;
}

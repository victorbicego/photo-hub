package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventDto {

  @NotNull private String name;

  @NotNull private LocalDateTime startDate;

  @NotNull private LocalDateTime endDate;
}

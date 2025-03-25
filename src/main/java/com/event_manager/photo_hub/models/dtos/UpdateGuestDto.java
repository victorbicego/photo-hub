package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGuestDto {

  @Size(min = 1, max = 50)
  @NotNull
  private String firstName;

  @Size(min = 1, max = 50)
  @NotNull
  private String lastName;
}

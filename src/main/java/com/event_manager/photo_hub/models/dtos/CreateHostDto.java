package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateHostDto {

  private String username;

  @NotNull
  @Size(min = 8)
  private String password;
}

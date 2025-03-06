package com.event_manager.photo_hub.models.dtos;

import com.event_manager.photo_hub.models.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestDto {

  private static final Role ROLE = Role.GUEST;

  @NotNull
  private Long id;

  @NotNull
  @Email
  private String username;

  @NotNull
  private Boolean enabled;

  @Size(min = 1, max = 50)
  @NotNull
  private String firstName;

  @Size(min = 1, max = 50)
  @NotNull
  private String lastName;
}

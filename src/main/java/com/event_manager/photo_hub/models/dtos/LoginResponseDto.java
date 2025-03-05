package com.event_manager.photo_hub.models.dtos;

import com.event_manager.photo_hub.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

  @NotNull
  @JsonIgnore
  private Cookie cookie;

  @NotNull @Email private String username;

  @NotNull private Role role;
}

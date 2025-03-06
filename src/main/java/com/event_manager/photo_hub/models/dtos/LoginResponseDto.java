package com.event_manager.photo_hub.models.dtos;

import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.event_manager.photo_hub.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

  @NotNull @JsonIgnore private Cookie cookie;

  @NotNull @Email private String username;

  @NotNull private Role role;
}

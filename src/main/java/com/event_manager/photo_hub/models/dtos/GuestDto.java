package com.event_manager.photo_hub.models.dtos;

import com.event_manager.photo_hub.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestDto {

  private static final Role ROLE = Role.GUEST;

  private Long id;

  private String username;
  private String password;
  private boolean enabled;
}

package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateHostDto {

    @NotNull
    @Email
    private String username;

    @NotNull
    @Size(min = 8)
    private String password;

    @Size(min = 1, max = 50)
    @NotNull
    private String firstName;

    @Size(min = 1, max = 50)
    @NotNull
    private String lastName;
}

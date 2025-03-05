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
public class RegisterConfirmationDto {

    @NotNull
    @Email
    private String username;

    @NotNull
    @Size(min = 10, max = 10)
    private String code;
}

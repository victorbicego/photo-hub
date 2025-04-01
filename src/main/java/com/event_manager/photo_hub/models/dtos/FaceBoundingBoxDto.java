package com.event_manager.photo_hub.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceBoundingBoxDto {

    @NotNull
    private Float top;

    @NotNull
    private Float left;

    @NotNull
    private Float width;

    @NotNull
    private Float height;
}

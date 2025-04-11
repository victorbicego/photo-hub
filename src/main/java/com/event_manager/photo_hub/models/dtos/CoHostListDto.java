package com.event_manager.photo_hub.models.dtos;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoHostListDto {

  @NotNull private List<String> coHostEmails;
}

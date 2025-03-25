package com.event_manager.photo_hub.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

  @NotNull private HttpStatus status;

  private T data;

  @NotNull
  @Size(min = 1)
  private String message;
}

package com.event_manager.photo_hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PhotoHubApplication {

  public static void main(String[] args) {
    SpringApplication.run(PhotoHubApplication.class, args);
  }
}

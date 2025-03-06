package com.event_manager.photo_hub.controllers.privates.host;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/host")
@RequiredArgsConstructor
@Validated
public class HostController {
}

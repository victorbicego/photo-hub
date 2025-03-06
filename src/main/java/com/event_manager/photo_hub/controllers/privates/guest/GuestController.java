package com.event_manager.photo_hub.controllers.privates.guest;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guest")
@RequiredArgsConstructor
@Validated
public class GuestController {
}

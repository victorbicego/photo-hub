package com.event_manager.photo_hub.models.mappers;

import lombok.RequiredArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.models.dtos.PhotoDto;
import com.event_manager.photo_hub.models.entities.Photo;

@Service
@RequiredArgsConstructor
public class PhotoMapper {

    private final ModelMapper modelMapper;

    public PhotoDto toDto(Photo photo) {
        PhotoDto photoDto = modelMapper.map(photo, PhotoDto.class);
        String originalUrl = photoDto.getPhotoUrl();
        String encodedUrl = URLEncoder.encode(originalUrl, StandardCharsets.UTF_8);
        photoDto.setPhotoUrl(encodedUrl);
        return photoDto;
    }
}

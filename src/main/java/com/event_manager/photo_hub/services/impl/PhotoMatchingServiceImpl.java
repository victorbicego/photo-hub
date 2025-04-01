package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.models.dtos.MatchedPhotosResponse;
import com.event_manager.photo_hub.services.PhotoMatchingService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PhotoMatchingServiceImpl implements PhotoMatchingService {

  private final RestTemplate restTemplate;

  @Value("${photo.matching.api.url}")
  private String photoMatchingApiUrl;

  @Override
  public MatchedPhotosResponse getMatchedPhotos(String event, MultipartFile file) throws IOException {
    String url = photoMatchingApiUrl + "/match-photos?event=" + event;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", file.getResource());
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    return restTemplate.postForObject(url, requestEntity, MatchedPhotosResponse.class);
  }
}

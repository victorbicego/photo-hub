package com.event_manager.photo_hub.config;

import com.event_manager.photo_hub.exceptions.RateLimitExceededException;
import com.event_manager.photo_hub.services.JwtService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter implements Filter {

  private static final Integer BUCKET_CAPACITY = 1000;
  private static final Integer REFILL_TOKENS = 1000;
  private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);
  private static final Integer TOKENS_PER_REQUEST = 1;

  private final JwtService jwtService;
  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

  private Bucket createNewBucket() {
    Bandwidth limit =
        Bandwidth.classic(BUCKET_CAPACITY, Refill.greedy(REFILL_TOKENS, REFILL_PERIOD));
    return Bucket.builder().addLimit(limit).build();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String clientId = resolveClientIdentifier(httpRequest);

    Bucket bucket = cache.computeIfAbsent(clientId, k -> createNewBucket());

    if (bucket.tryConsume(TOKENS_PER_REQUEST)) {
      chain.doFilter(request, response);
    } else {
      throw new RateLimitExceededException("Request limit exceeded. Please try again later.");
    }
  }

  private String resolveClientIdentifier(HttpServletRequest request) {
    String token = jwtService.extractTokenFromCookies(request);
    if (token != null) {
      String username = jwtService.extractUsername(token);
      return (username != null && !username.isEmpty()) ? username : request.getRemoteAddr();
    } else {
      return request.getRemoteAddr();
    }
  }
}

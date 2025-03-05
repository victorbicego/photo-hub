package com.event_manager.photo_hub.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.event_manager.photo_hub.services.JwtService;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter implements Filter {

    private static final Integer BUCKET_CAPACITY = 100;
    private static final Integer REFILL_TOKENS = 100;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);
    private static final Integer TOKENS_PER_REQUEST = 1;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHENTICATION_TYPE = "Bearer ";
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
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(AUTHENTICATION_TYPE)) {
            String token = authorizationHeader.substring(AUTHENTICATION_TYPE.length());
            String username = jwtService.extractUsername(token);
            return (username != null && !username.isEmpty()) ? username : request.getRemoteAddr();
        } else {
            return request.getRemoteAddr();
        }
    }
}

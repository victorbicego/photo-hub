package com.event_manager.photo_hub.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;

public interface JwtService {

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    String getActiveUsername() throws InvalidJwtTokenException;

    String extractTokenFromCookies(HttpServletRequest request);

    Cookie createCookie(UserDetails userDetails);
}

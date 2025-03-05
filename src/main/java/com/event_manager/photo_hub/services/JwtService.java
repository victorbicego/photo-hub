package com.event_manager.photo_hub.services;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

  String extractUsername(String token);

  boolean isTokenValid(String token, UserDetails userDetails);

  String generateToken(UserDetails userDetails);

  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

  String getActiveUsername() throws InvalidJwtTokenException;

  String extractTokenFromCookies(HttpServletRequest request);

  Cookie createCookie(String jwtToken);
}

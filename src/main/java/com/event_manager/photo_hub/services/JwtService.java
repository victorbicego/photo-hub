package com.event_manager.photo_hub.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

  String extractUsername(String token);

  boolean isTokenValid(String token, UserDetails userDetails);

  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

  String getActiveUsername();

  String extractTokenFromCookies(HttpServletRequest request);

  Cookie createCookie(UserDetails userDetails);

  Cookie createCookie(String jwtToken);

  Cookie createLogoutCookie();

  Cookie createQrCodeCookie(String qrCode);

  String getActiveQrCode();

  Cookie createLogoutQrCodeCookie();
}

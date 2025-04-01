package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.services.JwtService;
import com.event_manager.photo_hub.services_crud.EventCrudService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class JwtServiceImpl implements JwtService {

  private static final String JWT_TOKEN_KEY = "JWT_TOKEN";
  private static final String QRCODE_KEY = "QR_CODE";
  private static final int COOKIE_EXPIRATION = 34560000; // Aproximadamente 400 dias

  private final String secretKey;

  public JwtServiceImpl(@Value("${jwt.secret}") String secretKey) {
    this.secretKey = secretKey;
  }

  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && userDetails.isEnabled();
  }

  private String generateToken(UserDetails userDetails) {
    return generateToken(Map.of(), userDetails);
  }

  @Override
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    long currentTimeMillis = System.currentTimeMillis();
    Date issuedAt = new Date(currentTimeMillis);
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(issuedAt)
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String getActiveUsername() throws InvalidJwtTokenException {
    HttpServletRequest request = getCurrentHttpRequest();
    String jwtToken = extractTokenFromCookies(request);
    if (jwtToken != null) {
      return extractUsername(jwtToken);
    } else {
      throw new InvalidJwtTokenException("JWT Token is not present or is invalid.");
    }
  }

  @Override
  public String getActiveQrCode() throws InvalidJwtTokenException {
    HttpServletRequest request = getCurrentHttpRequest();
    String qrCode = extractQrCodeFromCookies(request);
    if (qrCode != null) {
      return qrCode;
    } else {
      throw new InvalidJwtTokenException("QrCode is not present or is invalid.");
    }
  }

  @Override
  public Cookie createEventLogoutCookie() {
    return createCookie(QRCODE_KEY, "", 0);
  }

  private String getCookieValue(HttpServletRequest request, String cookieName) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookieName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  @Override
  public String extractTokenFromCookies(HttpServletRequest request) {
    return getCookieValue(request, JWT_TOKEN_KEY);
  }

  private String extractQrCodeFromCookies(HttpServletRequest request) {
    return getCookieValue(request, QRCODE_KEY);
  }

  @Override
  public Cookie createCookie(UserDetails userDetails) {
    String jwtToken = generateToken(userDetails);
    return createCookie(JWT_TOKEN_KEY, jwtToken, COOKIE_EXPIRATION);
  }

  @Override
  public Cookie createCookie(String jwtToken) {
    return createCookie(JWT_TOKEN_KEY, jwtToken, COOKIE_EXPIRATION);
  }

  @Override
  public Cookie createLogoutCookie() {
    return createCookie(JWT_TOKEN_KEY, "", 0);
  }

  @Override
  public Cookie createQrCodeCookie(String qrCode) {
    return createCookie(QRCODE_KEY, qrCode, COOKIE_EXPIRATION);
  }

  private HttpServletRequest getCurrentHttpRequest() {
    return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Key getSignInKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  private Cookie createCookie(String key, String value, int maxAge) {
    Cookie cookie = new Cookie(key, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setMaxAge(maxAge);
    return cookie;
  }
}

package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.exceptions.InvalidJwtTokenException;
import com.event_manager.photo_hub.services.JwtService;
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
  private static final int JWT_TOKEN_EXPIRATION = 315360000; // Apr. 10 years in seconds

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
    Date expirationDate = new Date(currentTimeMillis + JWT_TOKEN_EXPIRATION * 1000L);

    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(issuedAt)
        .setExpiration(expirationDate)
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
  public String extractTokenFromCookies(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (JWT_TOKEN_KEY.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  @Override
  public Cookie createCookie(UserDetails userDetails) {
    String jwtToken = generateToken(userDetails);
    Cookie cookie = new Cookie(JWT_TOKEN_KEY, jwtToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(JWT_TOKEN_EXPIRATION);
    return cookie;
  }

  private HttpServletRequest getCurrentHttpRequest() {
    return ((ServletRequestAttributes)
            Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
        .getRequest();
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
}

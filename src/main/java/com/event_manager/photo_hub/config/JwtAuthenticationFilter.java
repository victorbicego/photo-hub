package com.event_manager.photo_hub.config;

import com.event_manager.photo_hub.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String jwtToken = jwtService.extractTokenFromCookies(request);

    if (jwtToken != null) {
      try {
        String username = jwtService.extractUsername(jwtToken);
        if (username != null && isNotAuthenticated()) {
          authenticateUser(request, response, jwtToken, username);
        }
      } catch (Exception ex) {
        response.addCookie(jwtService.createLogoutCookie());

        if (isProtectedEndpoint(request)) {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication token.");
          return;
        }
      }
    }
    filterChain.doFilter(request, response);
  }

  private boolean isProtectedEndpoint(HttpServletRequest request) {
    String uri = request.getRequestURI();
    if (uri.startsWith("/api/v1/host/")) {
      return true;
    }
    return false;
  }

  private boolean isNotAuthenticated() {
    return SecurityContextHolder.getContext().getAuthentication() == null;
  }

  private void authenticateUser(
      HttpServletRequest request, HttpServletResponse response, String jwtToken, String username) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    if (jwtService.isTokenValid(jwtToken, userDetails)) {
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);

      response.addCookie(jwtService.createCookie(jwtToken));
    }
  }
}

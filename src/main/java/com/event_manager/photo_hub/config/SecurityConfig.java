package com.event_manager.photo_hub.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final RateLimitingFilter rateLimitingFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        requests ->
                                requests
                                        .requestMatchers(
                                                "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/api/v1/**")
                                        .permitAll()
                                        .requestMatchers("/api/v1/admin/**")
                                        .hasAuthority("ADMIN")
                                        .requestMatchers("/api/v1/employee/**")
                                        .hasAnyAuthority("ADMIN", "EMPLOYEE")
                                        .requestMatchers("/api/v1/consumer/**")
                                        .hasAuthority("CONSUMER")
                                        .anyRequest()
                                        .authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitingFilter, JwtAuthenticationFilter.class);
        return http.build();
    }
}

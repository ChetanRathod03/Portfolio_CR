package com.chetanrathod.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Minimal security posture for a stateless public-facing JSON API:
 * - /api/contact and /api/recruiter-feedback are intentionally public (that's the point of the form)
 * - CSRF protection is disabled because this API is stateless (no cookies/sessions) and only
 *   accepts JSON from a known frontend origin enforced via CORS - CSRF tokens protect
 *   cookie-authenticated browser sessions, which this API doesn't use.
 * - No admin/auth endpoints exist yet. When the admin dashboard is built, add a
 *   proper login (e.g. Spring Security form login or JWT) and lock /api/admin/**
 *   behind .authenticated() here instead of leaving it permitAll.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/contact", "/api/recruiter-feedback", "/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {}); // placeholder auth mechanism for any future authenticated routes

        return http.build();
    }
}

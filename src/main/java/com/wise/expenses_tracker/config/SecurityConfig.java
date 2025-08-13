package com.wise.expenses_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wise.expenses_tracker.security.config.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity // Enable web security for the application
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; // JWT authentication filter
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configures the security filter chain for HTTP requests.
     * This filter chain is responsible for configuring all HTTP security of the application.
     * 
     * @param http HttpSecurity builder for configuring web security
     * @return SecurityFilterChain the configured security filter chain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // Disable CSRF protection since we use JWT tokens (stateless authentication)
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization rules for HTTP requests
            .authorizeHttpRequests(auth -> auth
                // Allow public access to Swagger documentation
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Allow public access to authentication endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            
            // Configure session management to be stateless (no server-side sessions)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Register our custom authentication provider
            .authenticationProvider(authenticationProvider)
            
            // Add JWT filter before the standard username/password authentication filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            
            .build();
    }
}

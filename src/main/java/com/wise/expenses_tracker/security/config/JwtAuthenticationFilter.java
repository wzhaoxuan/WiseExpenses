package com.wise.expenses_tracker.security.config;

// Standard Java imports
import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wise.expenses_tracker.security.interfaces.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * JWT Authentication Filter
 * 
 * This filter intercepts every HTTP request to validate JWT tokens and establish
 * user authentication context. It extends OncePerRequestFilter to ensure it's
 * executed only once per request.
 * 
 * Main Responsibilities:
 * 1. Extract JWT token from Authorization header
 * 2. Validate the token and extract user information
 * 3. Load user details from database
 * 4. Set authentication context for the current request
 * 
 */
@Component // Marks this class as a Spring-managed component
@RequiredArgsConstructor // Lombok annotation - generates constructor with final fields
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Main filter method that processes each HTTP request for JWT authentication.
     * 
     * Authentication Flow:
     * 1. Extract Authorization header from request
     * 2. Check if header contains Bearer token
     * 3. Extract and validate JWT token
     * 4. Load user details from database
     * 5. Create authentication context
     * 6. Continue request processing
     * 
     * @param request the incoming HTTP request
     * @param response the HTTP response to be sent
     * @param filterChain the chain of filters to be executed
     * @throws ServletException if servlet-related error occurs
     * @throws IOException if I/O error occurs during request processing
     */
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain
        ) throws ServletException, IOException {

        // Step 1: Extract the Authorization header from the HTTP request
        final String authorizationHeader = request.getHeader("Authorization");
        final String jwtToken;      // Will store the extracted JWT token
        final String userName;      // Will store the username extracted from token

        // Step 2: Check if Authorization header exists and starts with "Bearer "
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // No token present - continue to next filter without authentication
            // This allows public endpoints to work normally
            filterChain.doFilter(request, response);
            return; // Exit early, no authentication needed
        }

        // Step 3: Extract the actual JWT token (remove "Bearer " prefix)
        // Authorization header format: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        jwtToken = authorizationHeader.substring(7); // Remove first 7 characters ("Bearer ")
        
        // Step 4: Extract username from the JWT token
        userName = jwtService.extractUserName(jwtToken);

        // Step 5: Check if we have a username AND user is not already authenticated
        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Step 6: Load user details from database using the extracted username
            // This ensures we have the latest user information and authorities
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            
            // Step 7: Validate the JWT token against the user details
            // Checks token expiration, signature, and user validity
            if(jwtService.isTokenValid(jwtToken, userDetails)) {
                
                // Step 8: Create authentication token for Spring Security
                // This represents a successful authentication with user details and authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,                    // Principal (the authenticated user)
                    null,               // Credentials (not needed for JWT)
                    userDetails.getAuthorities()   // User's roles and permissions
                );

                // Step 9: Set additional details about the authentication
                // Includes request information like IP address, session ID, etc.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 10: Set the authentication in Spring Security context
                // This makes the user "logged in" for the duration of this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            // If token is invalid, we don't set authentication (user remains unauthenticated)
        }

        // Step 11: Continue with the rest of the filter chain
        // Whether authentication succeeded or failed, we continue processing the request
        // If authentication failed, protected endpoints will return 401/403
        filterChain.doFilter(request, response);
    }
}

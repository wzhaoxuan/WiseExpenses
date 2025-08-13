package com.wise.expenses_tracker.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; 
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wise.expenses_tracker.model.UserEntity;
import com.wise.expenses_tracker.repository.UserRepository;
import com.wise.expenses_tracker.security.auth.AuthResponse;
import com.wise.expenses_tracker.security.auth.request.AuthRequest;
import com.wise.expenses_tracker.security.auth.request.RegisterRequest;
import com.wise.expenses_tracker.security.interfaces.JwtService;
import com.wise.expenses_tracker.security.user.Role;
import com.wise.expenses_tracker.service.interfaces.AuthenticateService;

import lombok.RequiredArgsConstructor;


/**
 * Implementation of the AuthenticateService interface.
 * 
 * This service handles user authentication operations including:
 * - User registration with password encryption
 * - User authentication with credential validation
 * - JWT token generation for authenticated users
 * 
 * Uses Spring Security for authentication management and password encoding.
 */
@Service 
@RequiredArgsConstructor // Lombok annotation to generate constructor with final fields
public class AuthenticateServiceImpl implements AuthenticateService {

    // Dependencies injected via constructor (RequiredArgsConstructor)
    
    /** Manages authentication operations (credential validation) */
    private final AuthenticationManager authenticationManager;
    
    /** Repository for database operations on user entities */
    private final UserRepository userRepository;
    
    /** Encodes and validates passwords securely */
    private final PasswordEncoder passwordEncoder;
    
    /** Generates and validates JWT tokens */
    private final JwtService jwtService;

    /**
     * Registers a new user in the system.
     * 
     * This method performs the following operations:
     * 1. Creates a new UserEntity with provided details
     * 2. Encodes the password for secure storage
     * 3. Assigns the default USER role
     * 4. Saves the user to the database
     * 5. Generates a JWT token for immediate authentication
     * 
     * @param registerRequest the registration request containing username and password
     * @return AuthResponse containing the JWT token for the newly registered user
     */
    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        // Build a new user entity with the provided registration details
        var user = UserEntity.builder()
                .username(registerRequest.getUsername()) // Set the username from request
                .password(passwordEncoder.encode(registerRequest.getPassword())) // Encrypt password
                .role(Role.USER) // Assign default user role
                .build();
        
        // Persist the new user to the database
        userRepository.save(user);

        // Generate JWT token for the newly registered user
        var jwtToken = jwtService.generateToken(user);
        
        // Return authentication response with the token
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }


    /**
     * Authenticates an existing user with their credentials.
     * 
     * This method performs the following operations:
     * 1. Validates user credentials using Spring Security's AuthenticationManager
     * 2. Retrieves the user entity from the database
     * 3. Generates a new JWT token for the authenticated user
     * 4. Returns the token in an AuthResponse
     * 
     * The authentication process will throw an exception if:
     * - Username doesn't exist
     * - Password is incorrect
     * - Account is locked or disabled
     * 
     * @param authRequest the authentication request containing username and password
     * @return AuthResponse containing the JWT token for the authenticated user
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        // Validate user credentials using Spring Security
        // This will throw an exception if authentication fails
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),  // Username to authenticate
                authRequest.getPassword()   // Plain text password (will be compared with encoded password)
            )
        );

        // Retrieve the authenticated user from the database
        // orElseThrow() will throw an exception if user is not found (shouldn't happen after successful auth)
        var user = userRepository.findByUsername(authRequest.getUsername()).orElseThrow();
        
        // Generate a new JWT token for the authenticated user
        var jwtToken = jwtService.generateToken(user);
        
        // Return the authentication response with the token
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

}

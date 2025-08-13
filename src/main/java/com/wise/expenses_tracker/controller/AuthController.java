package com.wise.expenses_tracker.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wise.expenses_tracker.security.auth.AuthResponse;
import com.wise.expenses_tracker.security.auth.request.AuthRequest;
import com.wise.expenses_tracker.security.auth.request.RegisterRequest;
import com.wise.expenses_tracker.service.interfaces.AuthenticateService;
import com.wise.expenses_tracker.service.interfaces.UserService;
import com.wise.expenses_tracker.transferObject.UserDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticateService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Logic to handle user registration
        // This should include saving the user and generating a token
        // For now, we will return a dummy response
        return ResponseEntity.ok(authService.register(registerRequest));

    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile() {
        UserDTO userDTO = userService.getCurrentUserDTO();
        return ResponseEntity.ok(userDTO);
    }
}

package com.wise.expenses_tracker.service.interfaces;
import com.wise.expenses_tracker.security.auth.AuthResponse;
import com.wise.expenses_tracker.security.auth.request.AuthRequest;
import com.wise.expenses_tracker.security.auth.request.RegisterRequest;

public interface AuthenticateService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse authenticate(AuthRequest authRequest);
}

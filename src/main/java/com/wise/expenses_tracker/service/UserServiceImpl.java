package com.wise.expenses_tracker.service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wise.expenses_tracker.model.UserEntity;
import com.wise.expenses_tracker.repository.UserRepository;
import com.wise.expenses_tracker.service.interfaces.UserService;
import com.wise.expenses_tracker.transferObject.UserDTO;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserEntity getCurrentUser() {
        // Get the current authentication from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is authenticated and not anonymous
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("No authenticated user found");
        }
        
        // Extract username from the authentication principal
        String username = authentication.getName();

        // Find and return the user entity from database
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Override
    public UserDTO getCurrentUserDTO() {
        UserEntity userEntity = getCurrentUser();
        return convertToUserDTO(userEntity);
    }

    private UserDTO convertToUserDTO(UserEntity userEntity) {
        if (userEntity == null) return null;
        return new UserDTO(userEntity.getUsername(), userEntity.getRole());
    }

}

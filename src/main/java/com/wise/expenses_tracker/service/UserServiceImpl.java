package com.wise.expenses_tracker.service;
import com.wise.expenses_tracker.model.UserEntity;
import com.wise.expenses_tracker.repository.UserRepository;
import com.wise.expenses_tracker.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity getCurrentUser() {
        // Logic to retrieve the current user, e.g., from the security context
        // This is a placeholder implementation
        return userRepository.findById(1L).orElse(null); // Replace with actual logic
    }

}

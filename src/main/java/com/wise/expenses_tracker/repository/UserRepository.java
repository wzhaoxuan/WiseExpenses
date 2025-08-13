package com.wise.expenses_tracker.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wise.expenses_tracker.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // Additional query methods can be defined here if needed

    Optional<UserEntity> findByUsername(String username);

}

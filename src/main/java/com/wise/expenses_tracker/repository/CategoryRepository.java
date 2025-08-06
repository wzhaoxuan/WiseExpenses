package com.wise.expenses_tracker.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wise.expenses_tracker.model.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    // Additional query methods can be defined here if needed

    Optional<CategoryEntity> findByName(String name);

}

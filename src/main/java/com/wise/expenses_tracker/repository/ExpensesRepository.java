package com.wise.expenses_tracker.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wise.expenses_tracker.model.ExpensesEntity;

@Repository
public interface ExpensesRepository extends JpaRepository<ExpensesEntity, Long> {
    // Additional query methods can be defined here if needed
    

}

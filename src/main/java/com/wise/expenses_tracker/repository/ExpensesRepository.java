package com.wise.expenses_tracker.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wise.expenses_tracker.model.ExpensesEntity;
import com.wise.expenses_tracker.model.UserEntity;

@Repository
public interface ExpensesRepository extends JpaRepository<ExpensesEntity, Long> {
    
    /**
     * Find all expenses for a specific user
     * @param user the user entity
     * @return list of expenses belonging to the user
     */
    List<ExpensesEntity> findByUser(UserEntity user);
    
    /**
     * Find a specific expense by ID and user (for security)
     * @param id the expense ID
     * @param user the user entity
     * @return optional containing the expense if found and belongs to user
     */
    Optional<ExpensesEntity> findByIdAndUser(Long id, UserEntity user);

    /**
     * Find all expenses for a specific user and category
     * Uses the relationship between ExpensesEntity and CategoryEntity
     * @param user the user entity
     * @param categoryName the name of the category
     * @return list of expenses belonging to the user and category
     */
    List<ExpensesEntity> findByUserAndCategoryEntity_Name(UserEntity user, String categoryName);

    /**
     * Sum all expenses for a specific user and category
     * 
     * This method uses a custom JPQL query because Spring Data JPA cannot
     * automatically generate sum operations from method names.
     * 
     * The query:
     * 1. Joins ExpensesEntity with CategoryEntity
     * 2. Filters by user and category name
     * 3. Sums the amount field
     * 4. Returns the total as Double (null if no expenses found)
     * 
     * @param categoryName the name of the category to sum expenses for
     * @param user the user whose expenses to sum
     * @return the total amount of expenses for the category and user, or 0.0 if none found
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM ExpensesEntity e " +
           "JOIN e.categoryEntity c " +
           "WHERE c.name = :categoryName AND e.user = :user")
    Double sumExpensesByCategoryAndUser(@Param("categoryName") String categoryName, 
                                       @Param("user") UserEntity user);
}

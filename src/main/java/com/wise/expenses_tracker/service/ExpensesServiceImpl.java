package com.wise.expenses_tracker.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wise.expenses_tracker.model.CategoryEntity;
import com.wise.expenses_tracker.model.ExpensesEntity;
import com.wise.expenses_tracker.repository.ExpensesRepository;
import com.wise.expenses_tracker.service.interfaces.CategoryService;
import com.wise.expenses_tracker.service.interfaces.ExpensesService;
import com.wise.expenses_tracker.service.interfaces.UserService;
import com.wise.expenses_tracker.transferObject.CategoryDTO;
import com.wise.expenses_tracker.transferObject.ExpensesDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpensesServiceImpl implements ExpensesService {
    private final ExpensesRepository expensesRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<ExpensesDTO> getAllExpenses() {
        // Get current authenticated user
        var currentUser = userService.getCurrentUser();
        
        // Return only expenses belonging to the current user
        return expensesRepository.findByUser(currentUser).stream()
                .map(this::convertToExpensesDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpensesDTO> getExpenseById(Long id) {
        // Get current authenticated user
        var currentUser = userService.getCurrentUser();
        
        // Return expense only if it belongs to the current user
        return expensesRepository.findByIdAndUser(id, currentUser)
                .map(this::convertToExpensesDTO);
    }

    @Override
    @Transactional
    public Optional<ExpensesDTO> deleteExpense(Long id){
        // Get current authenticated user
        var currentUser = userService.getCurrentUser();
        
        // Find and delete expense only if it belongs to the current user
        return expensesRepository.findByIdAndUser(id, currentUser).map(existingExpense -> {
            expensesRepository.delete(existingExpense);
            return convertToExpensesDTO(existingExpense);
        });
    }

    @Override
    @Transactional
    public Optional<ExpensesDTO> updateExpense(Long id, ExpensesDTO expensesDTO){
        // Get current authenticated user
        var currentUser = userService.getCurrentUser();
        
        // Find expense only if it belongs to the current user
        ExpensesEntity expenseEntity = expensesRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + id + " for current user"));
        
        populateExpenseEntity(expenseEntity, expensesDTO);
        expensesRepository.save(expenseEntity);
        return Optional.of(convertToExpensesDTO(expenseEntity));
    }

    @Override
    @Transactional
    public ExpensesDTO saveExpense(ExpensesDTO expensesDTO) {
        ExpensesEntity expenseEntity = new ExpensesEntity();
        populateExpenseEntity(expenseEntity, expensesDTO);
        expensesRepository.save(expenseEntity);
        return convertToExpensesDTO(expenseEntity);
    }


    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getCategoryExpenses(){
        Map<String, Double> categoryExpenses = new HashMap<>();
        var currentUser = userService.getCurrentUser();
        Set<String> categories = expensesRepository.findByUser(currentUser).stream()
                .map(ExpensesEntity::getCategoryEntity)
                .map(CategoryEntity::getName)
                .collect(Collectors.toSet());

        for(String category : categories) {
            Double total = expensesRepository.sumExpensesByCategoryAndUser(category, currentUser);
            categoryExpenses.put(category, total);
        }
        return categoryExpenses;
    }


    /**
     * Populates an ExpensesEntity with data from ExpensesDTO
     *
     * @param expenseEntity the entity to populate
     * @param expensesDTO the DTO containing the data
     */
    private void populateExpenseEntity(ExpensesEntity expenseEntity, ExpensesDTO expensesDTO) {
        expenseEntity.setTitle(expensesDTO.getTitle());
        expenseEntity.setDate(expensesDTO.getDate());
        expenseEntity.setPay_by(expensesDTO.getPay_by());
        expenseEntity.setAmount(expensesDTO.getAmount());
        expenseEntity.setDescription(expensesDTO.getDescription());
        expenseEntity.setUser(userService.getCurrentUser());
        handleCategoryAssignment(expenseEntity, expensesDTO);
    }

    /**
     * Handles category assignment logic for an expense entity
     * 
     * @param expenseEntity the entity to assign the category to
     * @param expensesDTO the DTO containing the category information
     */
    private void handleCategoryAssignment(ExpensesEntity expenseEntity, ExpensesDTO expensesDTO) {
        if (expensesDTO.getCategory() != null && expensesDTO.getCategory().getName() != null) {
            if (!categoryService.getCategoryByName(expensesDTO.getCategory().getName()).isPresent()) {
                categoryService.createNewCategory(expensesDTO.getCategory());
            }
            CategoryEntity categoryEntity = categoryService.getCategoryByName(expensesDTO.getCategory().getName())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + expensesDTO.getCategory().getName()));
            expenseEntity.setCategoryEntity(categoryEntity);
        } else {
            // Handle case where category is not provided
            throw new IllegalArgumentException("Category must be provided");
        }
    }

    /**
     * Converts an ExpensesEntity to ExpensesDTO
     * 
     * @param expenseEntity the entity to convert
     * @return the converted ExpensesDTO
     */
    private ExpensesDTO convertToExpensesDTO(ExpensesEntity expenseEntity) {
        return new ExpensesDTO(
                expenseEntity.getId(),
                expenseEntity.getTitle(),
                expenseEntity.getDate(),
                expenseEntity.getPay_by(),
                expenseEntity.getAmount(),
                expenseEntity.getDescription(),
                convertToCategoryDTO(expenseEntity.getCategoryEntity())
        );
    }

    /**
     * Converts a CategoryEntity to CategoryDTO
     * 
     * @param category the entity to convert
     * @return the converted CategoryDTO
     */
    private CategoryDTO convertToCategoryDTO(CategoryEntity category) {
        if (category == null) return null;
        return new CategoryDTO(category.getId(), category.getName());
    }
}

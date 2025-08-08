package com.wise.expenses_tracker.service;
import java.util.List;
import java.util.Optional;
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

@Service
public class ExpensesServiceImpl implements ExpensesService {
    private final ExpensesRepository expensesRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    public ExpensesServiceImpl(ExpensesRepository expensesRepository, CategoryService categoryService, UserService userService) {
        this.expensesRepository = expensesRepository;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpensesDTO> getAllExpenses() {
        return expensesRepository.findAll().stream()
                .map(expense -> new ExpensesDTO(
                        expense.getId(),
                        expense.getTitle(),
                        expense.getDate(),
                        expense.getPay_by(),
                        expense.getAmount(),
                        expense.getDescription(),
                        convertToCategoryDTO(expense.getCategoryEntity())
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpensesDTO> getExpenseById(Long id) {
        return expensesRepository.findById(id).map(expense -> new ExpensesDTO(
                expense.getId(),
                expense.getTitle(),
                expense.getDate(),
                expense.getPay_by(),
                expense.getAmount(),
                expense.getDescription(),
                convertToCategoryDTO(expense.getCategoryEntity())
        ));
    }

    @Override
    @Transactional
    public Optional<ExpensesDTO> deleteExpense(Long id){
        return expensesRepository.findById(id).map(existingExpense -> {
            expensesRepository.delete(existingExpense);
            return new ExpensesDTO(existingExpense.getId(), existingExpense.getTitle(), existingExpense.getDate(),
                    existingExpense.getPay_by(), existingExpense.getAmount(), existingExpense.getDescription(), convertToCategoryDTO(existingExpense.getCategoryEntity()));
        });
    }

    @Override
    @Transactional
    public Optional<ExpensesDTO> updateExpense(Long id, ExpensesDTO expensesDTO){
        ExpensesEntity expenseEntity = expensesRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + id));
        expenseEntity.setTitle(expensesDTO.getTitle());
        expenseEntity.setDate(expensesDTO.getDate());
        expenseEntity.setPay_by(expensesDTO.getPay_by());
        expenseEntity.setAmount(expensesDTO.getAmount());
        expenseEntity.setDescription(expensesDTO.getDescription());
        expenseEntity.setUser(userService.getCurrentUser());
        if (expensesDTO.getCategory() != null && expensesDTO.getCategory().getName() != null) {
            CategoryEntity categoryEntity = categoryService.getCategoryByName(expensesDTO.getCategory().getName())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + expensesDTO.getCategory().getName()));
            expenseEntity.setCategoryEntity(categoryEntity);
        } else {
            // Handle case where category is not provided
            throw new IllegalArgumentException("Category must be provided");
        }

        expensesRepository.save(expenseEntity);

        return Optional.of(new ExpensesDTO(
                expenseEntity.getId(),
                expenseEntity.getTitle(),
                expenseEntity.getDate(),
                expenseEntity.getPay_by(),
                expenseEntity.getAmount(),
                expenseEntity.getDescription(),
                convertToCategoryDTO(expenseEntity.getCategoryEntity())
        ));
    }

    @Override
    @Transactional
    public ExpensesDTO saveExpense(ExpensesDTO expensesDTO) {
        ExpensesEntity expenseEntity = new ExpensesEntity();
        expenseEntity.setTitle(expensesDTO.getTitle());
        expenseEntity.setDate(expensesDTO.getDate());
        expenseEntity.setPay_by(expensesDTO.getPay_by());
        expenseEntity.setAmount(expensesDTO.getAmount());
        expenseEntity.setDescription(expensesDTO.getDescription());
        expenseEntity.setUser(userService.getCurrentUser());

        if (expensesDTO.getCategory() != null && expensesDTO.getCategory().getName() != null) {
            CategoryEntity categoryEntity = categoryService.getCategoryByName(expensesDTO.getCategory().getName())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + expensesDTO.getCategory().getName()));
            expenseEntity.setCategoryEntity(categoryEntity);
        } else {
            // Handle case where category is not provided
            throw new IllegalArgumentException("Category must be provided");
        }

        expensesRepository.save(expenseEntity);
        
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


    private CategoryDTO convertToCategoryDTO(CategoryEntity category) {
        if (category == null) return null;
        return new CategoryDTO(category.getId(), category.getName());
    }
}

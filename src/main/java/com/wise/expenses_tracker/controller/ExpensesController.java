package com.wise.expenses_tracker.controller;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wise.expenses_tracker.service.interfaces.ExpensesService;
import com.wise.expenses_tracker.transferObject.ExpensesDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExpensesController {
    private final ExpensesService expensesService;

    @GetMapping("/expenses")
    @Operation(summary = "Get all expenses", description = "Retrieve a list of all expenses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No expenses found")
    })
    public List<ExpensesDTO> getAllExpenses() {
        return expensesService.getAllExpenses();
    }

    @GetMapping("/expenses/{id}")
    @Operation(summary = "Get expense by ID", description = "Retrieve an expense by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expense found"),
        @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<ExpensesDTO> getExpenseById(@PathVariable Long id) {
        return expensesService.getExpenseById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/expenses/category")
    @Operation(
        summary = "Get expense totals by category", 
        description = "Retrieve the total amount spent in each category for the current user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category totals retrieved successfully"),
        @ApiResponse(responseCode = "200", description = "Empty result - user has no expenses yet"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<Map<String, Double>> getExpensesByCategory() {
        Map<String, Double> categoryExpenses = expensesService.getCategoryExpenses();
        
        // Return 200 with empty map instead of 404
        // Empty expenses is a valid state, not an error
        return ResponseEntity.ok(categoryExpenses);
    }

    @PostMapping("/expenses")
    @Operation(summary = "Create a new expense", description = "Add a new expense to the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Expense created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ExpensesDTO> createExpense(@Valid @RequestBody ExpensesDTO expensesDTO) throws URISyntaxException {
        ExpensesDTO savedExpense = expensesService.saveExpense(expensesDTO);
        return ResponseEntity.created(new URI("/api/expenses/" + savedExpense.getId()))
                .body(savedExpense);
        
    }

    @PutMapping("/expenses/{id}")
    @Operation(summary = "Update an existing expense", description = "Update an expense by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expense updated successfully"),
        @ApiResponse(responseCode = "404", description = "Expense not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ExpensesDTO> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpensesDTO expensesDTO) {
        return expensesService.updateExpense(id, expensesDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/expenses/{id}")
    @Operation(summary = "Delete an expense", description = "Delete an expense by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Expense deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Expense not found")
    }) 
    public ResponseEntity<ExpensesDTO> deleteExpense(@PathVariable Long id) {
        return expensesService.deleteExpense(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

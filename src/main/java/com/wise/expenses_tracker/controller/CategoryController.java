package com.wise.expenses_tracker.controller;
import java.net.URISyntaxException;
import java.util.List;
import java.net.URI;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wise.expenses_tracker.service.interfaces.CategoryService;
import com.wise.expenses_tracker.transferObject.CategoryDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get all categories", description = "Retrieve a list of all categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No categories found")
    })
    @GetMapping("/categories")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategoriesEntity();
    }

    @Operation(summary = "Get category by ID", description = "Retrieve a category by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found")  
    })
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id){
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new category", description = "Add a new category to the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category data provided"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
    })
    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws URISyntaxException{
        CategoryDTO saveCategory = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.created(new URI("/api/categories/" + saveCategory.getId()))
                .body(saveCategory);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategory(id, categoryDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

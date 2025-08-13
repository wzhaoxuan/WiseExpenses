package com.wise.expenses_tracker.service;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wise.expenses_tracker.model.CategoryEntity;
import com.wise.expenses_tracker.repository.CategoryRepository;
import com.wise.expenses_tracker.service.interfaces.CategoryService;
import com.wise.expenses_tracker.transferObject.CategoryDTO;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDTO saveCategory(CategoryDTO category) {
        return handleCategoryAssignment(category);
    }

    @Override
    @Transactional
    public CategoryDTO createNewCategory(CategoryDTO category) {
        return handleCategoryAssignment(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategoriesEntity(){
        Collection<CategoryEntity> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(this::convertToCategoryDTO)
                .collect(Collectors.toList());
        return categoryDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> getCategoryById(Long id){
        return categoryRepository.findById(id)
                .map(this::convertToCategoryDTO);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryEntity> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    @Transactional
    public Optional<CategoryDTO> updateCategory(Long id, CategoryDTO category) {
        return categoryRepository.findById(id).map(existingCategory -> {
            existingCategory.setName(category.getName());
            categoryRepository.save(existingCategory);
            return convertToCategoryDTO(existingCategory);
        });
    }

    @Override
    @Transactional
    public Optional<CategoryDTO> deleteCategory(Long id) {
        return categoryRepository.findById(id).map(existingCategory -> {
            categoryRepository.delete(existingCategory);
            return convertToCategoryDTO(existingCategory);
        });
    }


    private CategoryDTO handleCategoryAssignment(CategoryDTO categoryDTO) {
        CategoryEntity categoryName = new CategoryEntity();
        categoryName.setName(categoryDTO.getName());
        categoryRepository.save(categoryName);
        return new CategoryDTO(categoryName.getId(), categoryName.getName());
    }

    private CategoryDTO convertToCategoryDTO(CategoryEntity category) {
            if (category == null) return null;
            return new CategoryDTO(category.getId(), category.getName());
    }
}

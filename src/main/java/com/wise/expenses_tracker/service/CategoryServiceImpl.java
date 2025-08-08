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
;

@Service
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryDTO saveCategory(CategoryDTO category) {
        CategoryEntity categoryName = new CategoryEntity();
        categoryName.setName(category.getName());
        categoryRepository.save(categoryName);
        return new CategoryDTO(categoryName.getId(), categoryName.getName());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategoriesEntity(){
        Collection<CategoryEntity> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream().map(category -> new CategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
        return categoryDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> getCategoryById(Long id){
        return categoryRepository.findById(id)
                .map(category -> new CategoryDTO(category.getId(), category.getName()));
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
            return new CategoryDTO(existingCategory.getId(), existingCategory.getName());
        });
    }

    @Override
    @Transactional
    public Optional<CategoryDTO> deleteCategory(Long id) {
        return categoryRepository.findById(id).map(existingCategory -> {
            categoryRepository.delete(existingCategory);
            return new CategoryDTO(existingCategory.getId(), existingCategory.getName());
        });
    }
}

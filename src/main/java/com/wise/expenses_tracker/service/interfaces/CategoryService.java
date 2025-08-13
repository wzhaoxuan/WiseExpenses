package com.wise.expenses_tracker.service.interfaces;
import java.util.List;
import java.util.Optional;

import com.wise.expenses_tracker.transferObject.CategoryDTO;
import com.wise.expenses_tracker.model.CategoryEntity;

public interface CategoryService {

    CategoryDTO saveCategory(CategoryDTO category);
    CategoryDTO createNewCategory(CategoryDTO category);
    Optional<CategoryDTO> updateCategory(Long id, CategoryDTO category);
    Optional<CategoryDTO> deleteCategory(Long id);
    List<CategoryDTO> getAllCategoriesEntity();
    Optional<CategoryDTO> getCategoryById(Long id);
    Optional<CategoryEntity> getCategoryByName(String name);

}

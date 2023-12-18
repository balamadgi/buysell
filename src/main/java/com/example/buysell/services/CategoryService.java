package com.example.buysell.services;

import com.example.buysell.models.Category;
import com.example.buysell.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> listOfCategories() {
        return categoryRepository.findAll();
    }

    public void createCategory(String categoryTitle) {
        Category category = new Category();
        category.setTitle(categoryTitle);
        categoryRepository.save(category);
    }

    public void saveCategoryByTitle(String titleOfCategory) {
        Category category = new Category();
        category.setTitle(titleOfCategory);
        categoryRepository.save(category);
    }

    public void editCategory(Long id, String newCategoryTitle) {
      Category category = categoryRepository.findFirstById(id);
      category.setTitle(newCategoryTitle);
      categoryRepository.save(category);
        System.out.println(category.getTitle());

    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}

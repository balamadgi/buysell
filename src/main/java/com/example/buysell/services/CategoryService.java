package com.example.buysell.services;

import com.example.buysell.models.Category;
import com.example.buysell.models.Image;
import com.example.buysell.repositories.CategoryRepository;
import com.example.buysell.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;

    public List<Category> listOfCategories() {
        return categoryRepository.findAll();
    }

    public void createCategory( String categoryTitle, MultipartFile file) throws IOException {
        Category category = new Category();
        category.setTitle(categoryTitle);
        Image image;
        if (file.getSize() != 0) {
            image = toImageEntity(file);
            category.addImageToCategory(image);
        }
        categoryRepository.save(category);
    }
    public void editCategoryTitle(Long id, String newCategoryTitle) {
        Category category = categoryRepository.findFirstById(id);
        category.setTitle(newCategoryTitle);
        categoryRepository.save(category);
        System.out.println(category.getTitle());
    }

    private Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public void saveCategoryByTitle(String titleOfCategory) {
        Category category = new Category();
        category.setTitle(titleOfCategory);
        categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

}

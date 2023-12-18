package com.example.buysell.repositories;

import com.example.buysell.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByTitle (String title);
    Category findFirstById (Long id);


}

package com.example.buysell.repositories;

import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTitle (String title);
    Product findFirstById (Long id);

    List<Product> findByCategoryId (Long categoryId);

    @Query("SELECT p FROM Product p WHERE CONCAT(p.title, p.city, p.description, p.price) LIKE %?1%")
    public List<Product> search(String keyword);

    Page<Product> findAllByUser(User user, Pageable pageable);

    Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable);
}
                                         
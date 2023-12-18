package com.example.buysell.services;

import com.example.buysell.models.Category;
import com.example.buysell.models.Image;
import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import com.example.buysell.repositories.CategoryRepository;
import com.example.buysell.repositories.ImageRepository;
import com.example.buysell.repositories.ProductRepository;
import com.example.buysell.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> productsListByKeyword(String keyword) {
        if (keyword != null) {
            return productRepository.search(keyword);
        }
        return productRepository.findAll();
    }

    public Page<Product> listAll(int pageNum, int pageSize, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending());

        return productRepository.findAll(pageable);
    }

    public Page<Product> listAllByUser(User user, int pageNum, int pageSize, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending());

        return productRepository.findAllByUser(user, pageable);
    }

    public Page<Product> listAllByCategoryId(Long categoryId, int pageNum, int pageSize, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending());

        return productRepository.findAllByCategoryId(categoryId, pageable);
    }


    public void saveProduct(Principal principal,
                            Product product,
                            MultipartFile file1,
                            MultipartFile file2,
                            MultipartFile file3,
                            String categoryTitle) throws IOException {
        product.setUser(getUserByPrincipal(principal));
        product.setCategory(getCategoryByTitle(categoryTitle));

        addImage(file1, product, true);
        addImage(file2, product, false);
        addImage(file3, product, false);

        log.info("Saving new Product. Title: {}; Author: {}", product.getTitle(),
                product.getUser().getEmail());

        Product productFromDb = productRepository.save(product);
        if (!productFromDb.getImages().isEmpty()) {
            productFromDb.setPreviewImageId(productFromDb.getImages().get(0).getId());
        }
        productRepository.save(product);
    }

    private void addImage(MultipartFile file, Product product, boolean previewImg) throws IOException {
        Image image;
        if (file.getSize() != 0) {
            image = toImageEntity(file);
            image.setPreviewImage(previewImg);
            product.addImageToProduct(image);
        }
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

    public void editProduct(Principal principal,
                            Product product,
                            String categoryTitle) throws IOException {
        product.setUser(getUserByPrincipal(principal));
        product.setCategory(getCategoryByTitle(categoryTitle));

        product.setImages(productRepository.findFirstById(product.getId()).getImages());

        if (product.getPreviewImageId() == null && !product.getImages().isEmpty()) {
            product.setPreviewImageId(product.getImages().get(0).getId());
        }
        productRepository.save(product);
    }

    public Category getCategoryByTitle(String categoryTitle) {
        return categoryRepository.findByTitle(categoryTitle);
    }

    public User getUserByPrincipal(Principal principal) {
        if (principal == null) return new User();
        return userRepository.findFirstByEmail(principal.getName());
    }

    public void deleteProduct(User user, Long id) {
        Product product = productRepository.findById(id)
                .orElse(null);
        if (product != null) {
            if (Objects.equals(product.getUser().getId(), user.getId())) {
                productRepository.deleteById(id);
                log.info("Product with id = {} was deleted", id);
            } else {
                log.error("User: {} haven't this product with id = {}", user.getEmail(), id);
            }
        } else {
            log.error("Product with id = {} is not found", id);
        }
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product getProductByImageId(Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        assert image != null;
        return image.getProduct();
    }

    public List<Product> getProductsByCategoryId (Long id) {
        return productRepository.findByCategoryId(id);
    }

}

package com.example.buysell.services;

import com.example.buysell.models.Image;
import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import com.example.buysell.repositories.ImageRepository;
import com.example.buysell.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
private final ImageRepository imageRepository;
private final ProductService productService;
private final ProductRepository productRepository;

    public void editImage(Long imageId, MultipartFile file) throws IOException {
        Image image = imageRepository.findFirstById(imageId);
        toImageEntity(file, image);
        imageRepository.save(image);
    }

    private void toImageEntity(MultipartFile file, Image image) throws IOException {
        if (file.getSize() != 0) {
            image.setName(file.getName());
            image.setOriginalFileName(file.getOriginalFilename());
            image.setContentType(file.getContentType());
            image.setSize(file.getSize());
            image.setBytes(file.getBytes());
        }
    }

    public void deleteImage(User user, Long id) {
        Product product = productService.getProductByImageId(id);

        if (product != null) {
            if (Objects.equals(product.getUser().getId(), user.getId())) {

                imageRepository.deleteById(id);
                if (product.getImages().isEmpty()) {
                    product.setPreviewImageId(null);
                    productRepository.save(product);
                }
                else {
                    product.setPreviewImageId(product.getImages().get(0).getId());
                    productRepository.save(product);
                }
                log.info("Image with id = {} was deleted", id);
            } else {
                log.error("User: {} haven't this product with image id = {}", user.getEmail(), id);
            }
        } else {
            log.error("Image with id = {} is not found", id);
        }
    }

    public void addImage (Long productId, MultipartFile file) throws IOException {
        if (file.getSize() != 0) {
            Product product = productService.getProductById(productId);
            Image image = new Image();
            toImageEntity(file, image);
            product.addImageToProduct(image);
            productRepository.save(product);
            if (product.getPreviewImageId() == null) {
                product.setPreviewImageId(product.getImages().get(0).getId());
            }
            productRepository.save(product);
        }
    }
}

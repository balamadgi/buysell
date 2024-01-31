package com.example.buysell.controllers;

import com.example.buysell.models.Image;
import com.example.buysell.models.Product;
import com.example.buysell.repositories.ImageRepository;
import com.example.buysell.services.ImageService;
import com.example.buysell.services.ProductService;
import com.example.buysell.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ImageController {
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final ProductService productService;
    private final UserService userService;

    @GetMapping(value = {"product/images/{id}",
            "user/product/images/{id}",
            "product/edit-start/images/{id}"})
    private ResponseEntity<?> getImageById(@PathVariable Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        try {
            return ResponseEntity.ok()
                    .header("filename", image.getOriginalFileName())
                    .contentType(MediaType.valueOf(image.getContentType()))
                    .contentLength(image.getSize())
                    .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                    "Image with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/image/delete/{id}")
    public String deleteImage(@PathVariable Long id, Principal principal) {
        Product product = productService.getProductByImageId(id);
        imageService.deleteImage(userService.getUserByPrincipal(principal), id);
        return "redirect:/product/edit-start/" + product.getId();
    }

    @PostMapping("/image/edit/{id}")
    public String editImage(@PathVariable Long id,
                            @RequestParam("file") MultipartFile file) throws IOException {
        Product product = productService.getProductByImageId(id);
        imageService.editImage(id, file);
        return "redirect:/product/edit-start/" + product.getId();
    }

    @PostMapping("/image/add/{id}")
    public String addImage(@PathVariable Long id,
                            @RequestParam("file") MultipartFile file) throws IOException {
        Product product = productService.getProductById(id);
        imageService.addImage(id, file);
        return "redirect:/product/edit-start/" + product.getId();
    }


}
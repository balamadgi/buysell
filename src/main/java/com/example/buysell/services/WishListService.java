package com.example.buysell.services;

import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import com.example.buysell.models.WishList;
import com.example.buysell.repositories.ProductRepository;
import com.example.buysell.repositories.WishListRepository;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class WishListService {
    private final ProductRepository productRepository;
    private final WishListRepository wishListRepository;
    private final UserService userService;

    private WishList createNewWishList(User user, Long productId) {
        WishList wishList = new WishList();
        Product newProduct = productRepository.findFirstById(productId);
        wishList.setUser(user);
        wishList.setProducts(Collections.singletonList(newProduct));
        wishListRepository.save(wishList);
        return wishList;
    }

    public void addProduct(User user, Long productId) {
        Product newProduct = productRepository.findFirstById(productId);
        WishList wishList = user.getWishList();

        if (wishList == null) wishList = createNewWishList(user, productId);

        List<Product> productsInWishList = wishList.getProducts();

        if (!productsInWishList.contains(newProduct)) {
            List<Product> newProductList = new ArrayList<>(productsInWishList);
            newProductList.add(newProduct);
            wishList.setProducts(newProductList);
            wishListRepository.save(wishList);
        }
    }

    public void deleteProduct(User user, Long productId) {
        WishList wishList = user.getWishList();
        Product productToDelete = productRepository.findFirstById(productId);
        assert wishList != null;
        List<Product> products = wishList.getProducts();

        if (products.contains(productToDelete)) {
            List<Product> newProductList = new ArrayList<>(products);
            newProductList.remove(productToDelete);
            wishList.setProducts(newProductList);
            wishListRepository.save(wishList);
        }
    }
}

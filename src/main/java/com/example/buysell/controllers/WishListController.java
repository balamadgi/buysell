package com.example.buysell.controllers;

import com.example.buysell.models.Category;
import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import com.example.buysell.models.WishList;
import com.example.buysell.repositories.UserRepository;
import com.example.buysell.services.CategoryService;
import com.example.buysell.services.ProductService;
import com.example.buysell.services.UserService;
import com.example.buysell.services.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WishListController {
    private final CategoryService categoryService;
    private final WishListService wishListService;
    private final UserService userService;
    private final ProductService productService;


    @ModelAttribute(value = "user")
    public User user(Principal principal) {
        return userService.getUserByPrincipal(principal);
    }

    @ModelAttribute(value = "categories")
    public List<Category> categories() {
        return categoryService.listOfCategories();
    }

    @ModelAttribute(value = "wishListSize")
    public int wishListSize(Principal principal) {
        WishList wishList = userService.getUserByPrincipal(principal).getWishList();
        if (wishList != null)
            return wishList.getProducts().size();
        else
            return 0;
    }

    @GetMapping("user/{id}/wishlist")
    public String wishList(@PathVariable Long id, Model model, Principal principal) {

        WishList wishList = userService.getUserByPrincipal(principal).getWishList();
        if(wishList == null) {
            model.addAttribute("listProducts", new ArrayList<>());
        }
        else {
            List<Product> listProducts = wishList.getProducts();
            model.addAttribute("listProducts", listProducts);
        }
            return "wish-list";
    }

    @GetMapping("/product/{id}/add_to_wishlist")
    public String addToWishListFromHomePage(@PathVariable Long id, Principal principal) {
        add(id, principal);
        return "redirect:/product/" + id;
    }

    @GetMapping("/product/{id}/del_from_wishlist")
    public String delFromWishListFromHomePage(@PathVariable Long id, Principal principal) {
        del(id, principal);
        return "redirect:/product/" + id;
    }


    private void add(Long id, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        wishListService.addProduct(user, id);
    }

    private void del(Long id, Principal principal) {
        wishListService.deleteProduct(userService.getUserByPrincipal(principal), id);
    }
}

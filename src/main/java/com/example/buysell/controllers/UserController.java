package com.example.buysell.controllers;

import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import com.example.buysell.models.WishList;
import com.example.buysell.services.CategoryService;
import com.example.buysell.services.ProductService;
import com.example.buysell.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @ModelAttribute(value = "user")
    public User user(Principal principal) {
        return userService.getUserByPrincipal(principal);
    }

    @ModelAttribute(value = "wishListSize")
    public int wishListSize(Principal principal) {
        WishList wishList = userService.getUserByPrincipal(principal).getWishList();
        if (wishList != null)
            return wishList.getProducts().size();
        else
            return 0;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @GetMapping("/register")
    public String registration() {
        return "register";
    }

    @PostMapping("/register")
    public String createUser(User user, Model model) {
        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "  User with email:" + user.getEmail() + " is already exists");
            return "register";
        }
        return "redirect:/login";
    }


    @GetMapping("/user/{userToView}")
    public String products(Model model, Principal principal,
                           @PathVariable("userToView") User userToView) {

        return userInfo(model, principal, userToView, 1, "title", "asc");
    }

    @GetMapping("/user/{userToView}/page/{pageNum}")
    public String userInfo(Model model, Principal principal,
                           @PathVariable("userToView") User userToView,
                           @PathVariable(name = "pageNum") int pageNum,
                           @Param("sortField") String sortField,
                           @Param("sortDir") String sortDir) {
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        model.addAttribute("userToView", userToView);
        model.addAttribute("categories", categoryService.listOfCategories());

        int pageSize = 5;

        Page<Product> page = productService.listAllByUser(userToView, pageNum, pageSize, sortField, sortDir);
        List<Product> listProducts = page.getContent();

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listProducts", listProducts);


        return "user-info";
    }
}

package com.example.buysell.controllers;

import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import com.example.buysell.models.emuns.Role;
import com.example.buysell.repositories.CategoryRepository;
import com.example.buysell.services.CategoryService;
import com.example.buysell.services.ImageService;
import com.example.buysell.services.ProductService;
import com.example.buysell.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {

        model.addAttribute("users", userService.listOfUsers());
        model.addAttribute("categories", categoryService.listOfCategories());
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "admin";
    }

    @PostMapping("/admin/user/ban/{id}")
    public String userBan(@PathVariable("id") Long id) {
        userService.banUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/user/edit/{user}")
    public String userEdit(@PathVariable("user") User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user-edit";
    }

    @PostMapping("/admin/user/edit")
    public String userEdit(@RequestParam("userId") User user,
                           @RequestParam Map<String, String> form) {
        userService.changeUserRoles(user, form);
        return "redirect:/admin";
    }

    @PostMapping("/admin/category/create")
    public String addCategory(@RequestParam("catImage") MultipartFile file, @RequestParam("title") String categoryTitle) throws IOException {
        categoryService.createCategory(categoryTitle, file);
        return "redirect:/admin";
    }

    @PostMapping("/admin/category/edit-title/{id}")
    public String editCategoryTitle(@PathVariable("id") Long id,
                               @RequestParam("newTitle") String newCategoryTitle) {
        categoryService.editCategoryTitle(id, newCategoryTitle);
        return "redirect:/admin";
    }

    @PostMapping("/admin/category/edit-img/{catId}")
    public String editImage(@PathVariable Long catId,
                            @RequestParam("newImage") MultipartFile file) throws IOException {

        imageService.editImage(categoryRepository.findFirstById(catId).getImage().getId(), file);
        return "redirect:/admin";
    }

    @PostMapping("/admin/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin";
    }
}

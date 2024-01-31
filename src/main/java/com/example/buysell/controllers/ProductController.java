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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;

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

    @GetMapping("/")
    public String productsAll(Model model) {
        return viewPage(model, 1, "title", "asc");
    }

    @GetMapping("/page/{pageNum}")
    public String viewPage(Model model,
                           @PathVariable(name = "pageNum") int pageNum,
                           @Param("sortField") String sortField,
                           @Param("sortDir") String sortDir) {

        int pageSize = 5;
        Page<Product> page = productService.listAll(pageNum, pageSize, sortField, sortDir);
        pagingToModel(page, pageNum, sortField, sortDir, model);

        return "home-page";
    }

    @GetMapping("/category/{id}")
    public String productsByCategory(Model model,
                                     @PathVariable("id") Long categoryId) {

        return ShowProductsByCategory(model, categoryId, 1, "title", "asc");
    }

    @GetMapping("/category/{id}/page/{pageNum}")
    public String ShowProductsByCategory(Model model,
                                         @PathVariable(name = "id") Long categoryId,
                                         @PathVariable(name = "pageNum") int pageNum,
                                         @Param("sortField") String sortField,
                                         @Param("sortDir") String sortDir) {

        int pageSize = 5;

        Page<Product> page = productService.listAllByCategoryId(categoryId, pageNum, pageSize, sortField, sortDir);
        pagingToModel(page, pageNum, sortField, sortDir, model);
        return "prod-category";
    }

    @GetMapping("/search")
    public String search(Model model, Principal principal,
                         @Param("keyword") String keyword) {
        List<Product> listProductsByKeyword = productService.productsListByKeyword(keyword);
        model.addAttribute("listProducts", listProductsByKeyword);

        model.addAttribute("keyword", keyword);
        return "search-result";
    }

    @GetMapping("/product-new")
    public String createProduct(Model model, Principal principal, @ModelAttribute("product") Product product) {
        return "product-new";
    }

    @PostMapping("/product-new")
    public String createProduct(@RequestParam("file1") MultipartFile file1,
                                @RequestParam("file2") MultipartFile file2,
                                @RequestParam("file3") MultipartFile file3,
                                @RequestParam("categoryTitle") String categoryTitle,
                                Principal principal,
                                @Valid @ModelAttribute("product") Product product, BindingResult bindingResult) throws IOException {
        product.setCategory(productService.getCategoryByTitle(categoryTitle));

        if (bindingResult.hasErrors()) {
            return "product-new";
        } else {

            productService.saveProduct(principal, product, file1, file2, file3, categoryTitle);
            return "redirect:/user/" + userService.getUserByPrincipal(principal).getId();
        }
    }

    @GetMapping("/product/edit-start/{id}")
    public String editProduct(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("startEdition", true);

        Product product = productService.getProductById(id);
        model.addAttribute("userToView", product.getUser());
        model.addAttribute("checkUser", product.getUser() == userService.getUserByPrincipal(principal));
        model.addAttribute("product", product);

        return "product-detail";
    }

    @PostMapping("/product/edit/{id}")
    public String editProduct(Principal principal,
                              @RequestParam("newCategoryTitle") String newCategoryTitle,
                              @ModelAttribute("newProduct") Product newProduct) throws IOException {

        productService.editProduct(principal, newProduct, newCategoryTitle);
        return "redirect:/product/edit-start/" + newProduct.getId();
    }

    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Principal principal) {
        productService.deleteProduct(userService.getUserByPrincipal(principal), id);
        return "redirect:/";
    }

    @GetMapping("/product/{id}")
    public String showDetailsOfProduct(@PathVariable Long id, Model model, Principal principal) {
        Product product = productService.getProductById(id);
        model.addAttribute("userToView", product.getUser());
        model.addAttribute("checkUser", product.getUser() == userService.getUserByPrincipal(principal));
        model.addAttribute("product", product);
        return "product-detail";
    }

    private void pagingToModel(Page<Product> page,
                               int pageNum,
                               String sortField,
                               String sortDir,
                               Model model) {
        List<Product> listProducts = page.getContent();

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listProducts", listProducts);
    }
}

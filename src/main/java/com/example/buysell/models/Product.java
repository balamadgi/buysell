package com.example.buysell.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Field product title can't be empty")
    private String title;

    @Column(length = 5000)
    private String description;

    @NotNull(message = "Field product price can't be empty!")
    @Min(value = 0, message = "Price should be grater then zero!")
    private int price;

    private String city;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "product")
    private List<Image> images = new ArrayList<>();
    private Long previewImageId;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn
    private User user;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn
    private Category category;

    public void addImageToProduct(Image image) {
        image.setProduct(this);
        images.add(image);
    }

    public boolean inWishList(User user) {
        if (user == null) return false;
        WishList wishList = user.getWishList();
        if (wishList == null) return false;
        else return wishList.getProducts().contains(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(title, product.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}

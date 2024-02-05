package com.example.buysell.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(columnDefinition = "text")
    private String name;
    @Column(columnDefinition = "text")
    private String originalFileName;

    private Long size;
    @Column(columnDefinition = "text")
    private String contentType;

    private boolean isPreviewImage;
    @Lob
    @Column(length = 16777215)
    private byte[] bytes;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Product product;

    @OneToOne
    private Category category;

}

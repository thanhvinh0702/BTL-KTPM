package com.ecommerce.productservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "category_name", nullable = false)
    private String categoryName;
    @Column(nullable = false)
    private String description;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    private Integer quantity = 0;
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
}

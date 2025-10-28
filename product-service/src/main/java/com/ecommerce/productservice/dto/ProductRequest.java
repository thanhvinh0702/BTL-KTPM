package com.ecommerce.productservice.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String categoryName;
    private String description;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Long ownerId;
}

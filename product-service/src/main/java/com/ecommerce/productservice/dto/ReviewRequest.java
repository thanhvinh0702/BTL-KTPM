package com.ecommerce.productservice.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private String comment;
    private Integer rating;
}

package com.ecommerce.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String comment;
    private Integer rating;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.ecommerce.productservice.mapper;

import com.ecommerce.productservice.dto.ReviewResponse;
import com.ecommerce.productservice.model.Review;
import com.ecommerce.productservice.model.ReviewProductIdIndex;
import org.springframework.stereotype.Component;

@Component
public class ReviewResponseMapper {

    public ReviewResponse mapReviewToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .comment(review.getComment())
                .rating(review.getRating())
                .userId(review.getUserId())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public ReviewResponse mapReviewProductIdIndexToReviewResponse(ReviewProductIdIndex reviewProductIdIndex) {
        return ReviewResponse.builder()
                .id(reviewProductIdIndex.getReviewId())
                .productId(reviewProductIdIndex.getProductId())
                .comment(reviewProductIdIndex.getComment())
                .rating(reviewProductIdIndex.getRating())
                .userId(reviewProductIdIndex.getUserId())
                .createdAt(reviewProductIdIndex.getCreatedAt())
                .updatedAt(reviewProductIdIndex.getUpdatedAt())
                .build();
    }
}

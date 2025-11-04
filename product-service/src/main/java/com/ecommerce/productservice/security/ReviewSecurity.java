package com.ecommerce.productservice.security;

import com.ecommerce.productservice.repository.ReviewRepository;
import org.springframework.stereotype.Component;

@Component("reviewSecurity")
public class ReviewSecurity {

    private final ReviewRepository reviewRepository;

    public ReviewSecurity(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public boolean isOwner(Long reviewId, Long userId) {
        return reviewRepository.findById(reviewId)
                .map(review -> review.getUserId().equals(userId))
                .orElse(false);
    }
}

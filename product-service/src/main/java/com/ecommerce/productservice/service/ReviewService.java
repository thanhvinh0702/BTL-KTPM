package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ReviewRequest;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.model.Review;
import com.ecommerce.productservice.repository.ReviewRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    public ReviewService(ReviewRepository reviewRepository, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
    }

    public Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() ->
                new NoSuchElementException("Review with Id " + reviewId + " not found!"));
    }

    public List<Review> findByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Review createReview(Long productId, ReviewRequest reviewRequest, Long userId) {
        if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
            throw new IllegalArgumentException("Review rating must be between 1 and 5");
        }
        Product product = productService.findById(productId);
        Review review = Review.builder()
                .userId(userId)
                .rating(reviewRequest.getRating())
                .comment(reviewRequest.getComment())
                .product(product)
                .build();
        return reviewRepository.save(review);
    }

    @PreAuthorize("@reviewSecurity.isOwner(#reviewId, #userId)")
    public Review updateReview(Long reviewId, ReviewRequest reviewRequest, Long userId) {
        Review existedReview = this.findById(reviewId);
        if (reviewRequest.getComment() != null) {
            existedReview.setComment(reviewRequest.getComment());
        }
        if (reviewRequest.getRating() != null) {
            existedReview.setRating(reviewRequest.getRating());
        }
        return reviewRepository.save(existedReview);
    }

    @PreAuthorize("@reviewSecurity.isOwner(#reviewId, #userId)")
    public Review deleteReview(Long reviewId, Long userId) {
        Review existedReview = this.findById(reviewId);
        reviewRepository.deleteById(reviewId);
        return existedReview;
    }
}

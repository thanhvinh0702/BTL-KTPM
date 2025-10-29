package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.ReviewRequest;
import com.ecommerce.productservice.model.Review;
import com.ecommerce.productservice.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> findById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.findById(reviewId));
    }

    @GetMapping
    public ResponseEntity<List<Review>> findByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.findByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@PathVariable Long productId,
                                               @RequestBody ReviewRequest reviewRequest,
                                               @RequestHeader("x-user-id") Long userId) {
        return new ResponseEntity<>(reviewService.createReview(productId, reviewRequest, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long reviewId,
                                               @RequestBody ReviewRequest reviewRequest,
                                               @RequestHeader("x-user-id") Long userId) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewRequest, userId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Review> deleteReview(@PathVariable Long reviewId,
                                               @RequestHeader("x-user-id") Long userId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId, userId));
    }
}

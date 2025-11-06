package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.ReviewRequest;
import com.ecommerce.productservice.dto.ReviewResponse;
import com.ecommerce.productservice.model.Review;
import com.ecommerce.productservice.model.ReviewProductIdIndex;
import com.ecommerce.productservice.service.ReviewService;
import com.ecommerce.productservice.validation.OnCreate;
import com.ecommerce.productservice.validation.OnUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> findById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.findById(reviewId));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> findByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.findByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long productId,
                                               @Validated(OnCreate.class) @RequestBody ReviewRequest reviewRequest,
                                               @RequestHeader("x-user-id") Long userId) {
        return new ResponseEntity<>(reviewService.createReview(productId, reviewRequest, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long reviewId,
                                               @Validated(OnUpdate.class) @RequestBody ReviewRequest reviewRequest,
                                               @RequestHeader("x-user-id") Long userId) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewRequest, userId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> deleteReview(@PathVariable Long reviewId,
                                               @RequestHeader("x-user-id") Long userId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId, userId));
    }
}

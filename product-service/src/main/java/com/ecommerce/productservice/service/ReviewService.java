package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ReviewRequest;
import com.ecommerce.productservice.dto.ReviewResponse;
import com.ecommerce.productservice.mapper.ReviewResponseMapper;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.model.Review;
import com.ecommerce.productservice.model.ReviewProductIdIndex;
import com.ecommerce.productservice.model.ReviewProductIdIndexKey;
import com.ecommerce.productservice.repository.ReviewProductIdIndexRepository;
import com.ecommerce.productservice.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewProductIdIndexRepository reviewProductIdIndexRepository;
    private final ProductService productService;
    private final ReviewResponseMapper reviewResponseMapper;

    public Review findEntityById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() ->
                new NoSuchElementException("Review with Id " + reviewId + " not found!"));
    }

    public ReviewResponse findById(Long reviewId) {
        return reviewResponseMapper.mapReviewToReviewResponse(reviewRepository.findById(reviewId).orElseThrow(() ->
                new NoSuchElementException("Review with Id " + reviewId + " not found!")));
    }

    /**
     * Find review by product id (Using reviewProductIndex repository for efficient query)
     * @param productId: product id
     * @return reviews: List of review response
     */
    public List<ReviewResponse> findByProductId(Long productId) {
        return reviewProductIdIndexRepository.findByProductId(productId)
                .stream()
                .map(reviewResponseMapper::mapReviewProductIdIndexToReviewResponse)
                .toList();
    }

    @Transactional
    public ReviewResponse createReview(Long productId, ReviewRequest reviewRequest, Long userId) {
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
        Review savedReview = reviewRepository.save(review);
        ReviewProductIdIndex reviewProductIdIndex = com.ecommerce.productservice.model.ReviewProductIdIndex.builder()
                .reviewId(savedReview.getId())
                .productId(savedReview.getProduct().getId())
                .comment(savedReview.getComment())
                .rating(savedReview.getRating())
                .userId(savedReview.getUserId())
                .createdAt(savedReview.getCreatedAt())
                .updatedAt(savedReview.getUpdatedAt())
                .build();
        reviewProductIdIndexRepository.save(reviewProductIdIndex);
        return reviewResponseMapper.mapReviewToReviewResponse(savedReview);
    }

    @Transactional
    @PreAuthorize("@reviewSecurity.isOwner(#reviewId, #userId)")
    public ReviewResponse updateReview(Long reviewId, ReviewRequest reviewRequest, Long userId) {
        Review existedReview = this.findEntityById(reviewId);
        ReviewProductIdIndex existedReviewProductIdIndex = reviewProductIdIndexRepository.findById(
                new ReviewProductIdIndexKey(reviewId, existedReview.getProduct().getId())
        )
                .orElseThrow(() -> new NoSuchElementException("Review product id index with review id "
                        + reviewId + " and product id " + existedReview.getProduct().getId() + " not found!"));
        if (reviewRequest.getComment() != null) {
            existedReview.setComment(reviewRequest.getComment());
            existedReviewProductIdIndex.setComment(reviewRequest.getComment());
        }
        if (reviewRequest.getRating() != null) {
            if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
                throw new IllegalArgumentException("Review rating must be between 1 and 5");
            }
            existedReview.setRating(reviewRequest.getRating());
            existedReviewProductIdIndex.setRating(reviewRequest.getRating());
        }
        reviewProductIdIndexRepository.save(existedReviewProductIdIndex);
        return reviewResponseMapper.mapReviewToReviewResponse(reviewRepository.save(existedReview));
    }

    @Transactional
    @PreAuthorize("@reviewSecurity.isOwner(#reviewId, #userId)")
    public ReviewResponse deleteReview(Long reviewId, Long userId) {
        Review existedReview = this.findEntityById(reviewId);
        reviewRepository.deleteById(reviewId);
        reviewProductIdIndexRepository.deleteById(new ReviewProductIdIndexKey(reviewId, existedReview.getProduct().getId()));
        return reviewResponseMapper.mapReviewToReviewResponse(existedReview);
    }
}

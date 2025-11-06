package com.ecommerce.productservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class ReviewProductIdIndexKey implements Serializable {
    private Long reviewId;
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewProductIdIndexKey that)) return false;
        return Objects.equals(reviewId, that.reviewId) &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, productId);
    }
}


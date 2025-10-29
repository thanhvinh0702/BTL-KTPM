package com.ecommerce.productservice.dto;

import com.ecommerce.productservice.validation.OnCreate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotBlank(message = "Comment cannot be empty", groups = OnCreate.class)
    private String comment;

    @NotNull(message = "Rating is required", groups = OnCreate.class)
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
}

package com.ecommerce.productservice.dto;

import com.ecommerce.productservice.validation.OnCreate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "name cannot be empty", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "category name cannot be empty", groups = OnCreate.class)
    private String categoryName;

    @NotBlank(message = "description cannot be empty", groups = OnCreate.class)
    private String description;

    private String imageUrl;

    @NotNull(message = "price cannot be null", groups = OnCreate.class)
    @DecimalMin(value = "0.0", inclusive = true, message = "price cannot be negative")
    private Double price;

    @NotNull(message = "quantity cannot be null", groups = OnCreate.class)
    @Min(value = 0, message = "quantity cannot be negative")
    private Integer quantity;
}

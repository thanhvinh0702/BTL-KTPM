package com.ecommerce.cartservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    /**
     * được dùng bởi ProductClient
     */
    private Long id;
    private String name;
    private Double price;
    private String imageUrl;
    private Boolean available;
}

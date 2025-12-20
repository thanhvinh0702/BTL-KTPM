package com.ecommerce.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSaga {

    @Id
    private String sagaId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SagaStatus paymentStatus;

    @Column(name = "product_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SagaStatus productStatus;

    @Column(name = "cart_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SagaStatus cartStatus;

    @Column(name = "delivery_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SagaStatus deliveryStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

package com.ecommerce.paymentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "user_credit",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_credit_user_id",
                        columnNames = "user_id"
                )
        }
)
public class UserCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Builder.Default
    @Column(nullable = false)
    private Double credit = 0.0;
}

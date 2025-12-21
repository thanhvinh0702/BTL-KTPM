package com.ecommerce.shippingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
@Table(name = "Shipping")
public class ShippingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_id")
    private Long shippingId;

    @NotNull(message = "State Is Mandatory ,can Not Be Null")
    @NotBlank(message = "State Is Mandatory")
    @Column(name = "state")
    private String state;

    @NotNull(message = "Address Is Mandatory ,can Not Be Null")
    @NotBlank(message = "Address Is Mandatory")
    @Column(name = "street")
    private String street;

    @NotNull(message = "City Is Mandatory ,can Not Be Null")
    @NotBlank(message = "City Is Mandatory")
    @Column(name = "city")
    private String city;

    @Column(name = "flat_no")
    private String flatNo;

    @NotNull(message = "Zip Code Is Mandatory ,can Not Be Null")
    @NotBlank(message = "Zip Code Is Mandatory")
    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "shipper_id")
    private Integer shipperId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

}
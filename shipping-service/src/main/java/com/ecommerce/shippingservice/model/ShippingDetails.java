package com.ecommerce.shippingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "Shipping")
public class ShippingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_id")
    private Long shippingId;

    @NotNull(message = "Address Is Mandatory ,can Not Be Null")
    @NotBlank(message = "Address Is Mandatory")
    @Column(name = "address")
    private String address;

    @NotNull(message = "City Is Mandatory ,can Not Be Null")
    @NotBlank(message = "City Is Mandatory")
    @Column(name = "city")
    private String city;

    @NotNull(message = "State Is Mandatory ,can Not Be Null")
    @NotBlank(message = "State Is Mandatory")
    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @NotNull(message = "Postal Code Is Mandatory ,can Not Be Null")
    @NotBlank(message = "Postal Code Is Mandatory")
    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "shipper_id")
    private Integer shipperId;

    @Column(name = "order_id")
    private Integer orderID;

}
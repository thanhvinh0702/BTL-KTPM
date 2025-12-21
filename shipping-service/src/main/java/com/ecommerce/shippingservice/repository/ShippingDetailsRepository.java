package com.ecommerce.shippingservice.repository;

import com.ecommerce.shippingservice.model.ShippingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingDetailsRepository extends JpaRepository<ShippingDetails, Long> {
}

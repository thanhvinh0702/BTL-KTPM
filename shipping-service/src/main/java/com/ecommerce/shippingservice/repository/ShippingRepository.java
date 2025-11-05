package com.ecommerce.shippingservice.repository;
import com.ecommerce.shippingservice.model.ShippingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends JpaRepository<ShippingDetails, Integer> {

}

package com.ecommerce.shippingservice.repository;

import com.ecommerce.shippingservice.model.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Integer> {

}

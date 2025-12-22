package com.ecommerce.orderservice.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.orderservice.model.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o WHERE o.orderDate >= :date")
    List<Orders> findByOrderDateGreaterThanEqual(LocalDateTime date);

    List<Orders> findAllByUserId(String userId);

}

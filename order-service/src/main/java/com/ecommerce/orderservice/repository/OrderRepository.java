package com.ecommerce.orderservice.repository;

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
public interface OrderRepository extends JpaRepository<Orders, Integer> {

    @Query("SELECT o FROM Orders o WHERE o.orderId = :orderId AND o.userId = :userId")
    Orders findByIdAndCustomerId(@Param("orderId") Integer orderId, @Param("userId") Integer userId);

    @Query("SELECT o FROM Orders o WHERE o.orderDate >= :date")
    List<Orders> findByOrderDateGreaterThanEqual(Date date);

    @Query("SELECT o FROM Orders o WHERE  o.userId = :userId")
    List<Orders> getAllOrderByUserId(@Param("userId") Integer userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Orders o WHERE o.userID = :userID AND o.orderID = :orderID")
    int deleteByIdAndCustomerId(@Param("userID") Integer userID, @Param("orderID") Integer orderID);
}

package com.ecommerce.orderservice.service;

import java.util.Date;
import java.util.List;

import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.ecommerce.orderservice.model.Orders;


@Service
public class OrdersService {
    private OrderRepository orderRepository;

    /***
    public OrderResponse placeOrder(Long userId){

    };
    public Orders updateOrders(Integer ordersid,OrderResponse orderDTo){

    };
    public void deleteOrders(Integer userId,Integer Orderid){

    };
     ***/


    public Orders getOrdersDetails(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found in the database."));
    };

    /// no exception here because function can return a [] list
    public List<Orders> getAllUserOrder(Integer userId) {
        List<Orders> orders = orderRepository.getAllOrderByUserId(userId);
        if (orders.isEmpty()) {
            throw new EntityNotFoundException("No orders found for the user in the database.");
        }
        return orders;
    };

    public List<Orders> viewAllOrders(){
        List<Orders> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            throw new EntityNotFoundException("No orders found in the database.");
        }
        return orders;
    };

    public List<Orders> viewAllOrderByDate(Date date){
        List<Orders> orders = orderRepository.findByOrderDateGreaterThanEqual(date);

        if (orders.isEmpty()) {
            throw new EntityNotFoundException("No orders found for the given date.");
        }
        return orders;
    };


}

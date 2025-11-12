package com.ecommerce.orderservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ecommerce.orderservice.dto.CartItemResponse;
import com.ecommerce.orderservice.dto.CartResponse;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.client.CartClient;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.ecommerce.orderservice.model.Orders;


@Service
public class OrdersService {
    private OrderRepository orderRepository;
    private CartClient cartClient;

    @Transactional
    public OrderResponse placeOrder(Long userId){
        /**
         * get user cart
         */
        CartResponse cartResponse = cartClient.getCartByUserId(userId);

        if(cartResponse.getItems().isEmpty()){
            throw new RuntimeException("No items in order");
        }
        Orders order = new Orders();
        order.setOrderDate(LocalDateTime.now());
        List<CartItemResponse> cartItems = cartResponse.getItems();
        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItemResponse cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getOrderId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProduct_id(cartItem.getProductId());
            orderItems.add(orderItem);
        }

        order.setOrderItem(orderItems);
        order.setTotalAmount(cartResponse.getTotalAmount());
        orderRepository.save(order);

        OrderResponse orderData=new OrderResponse();
        orderData.setOrderId(order.getOrderId());
        orderData.setOrderAmount(order.getTotalAmount());
        orderData.setStatus("Pending");
        orderData.setPaymentStatus("Pending");
        orderData.setOrderDate("Current" + "Date");
        return orderData;

    };
    
    @Transactional
    public Orders updateOrders(Integer orderId, OrderResponse orderDTo){
        return null;
    };
    
    @Transactional
    public void deleteOrders(Integer userId,Integer orderId){
        int deletedCount = orderRepository.deleteByIdAndCustomerId(userId, orderId);
        if (deletedCount == 0) {
            throw new EntityNotFoundException("Order not found");
        }
    };


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

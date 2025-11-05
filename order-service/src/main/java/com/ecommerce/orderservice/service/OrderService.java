package com.ecommerce.orderservice.service;

import java.util.Date;
import java.util.List;

import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.orderservice.OrdersException;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.model.Orders;
import com.ecommerce.orderservice.model.OrderItem;

@Service
public class OrdersService {
    private OrderRepository orderRepository;
    ///  waiting for another service
    public OrderResponse placeOrder(Integer orderId) throws OrdersException{
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User Not Found In Database"));

        Cart usercart = existingUser.getCart();
        if(usercart.getTotalAmount()==0){
            throw new OrdersException("Add item To the cart first.......");
        }
        Integer cartId = usercart.getCartId();

        Orders newOrder = new Orders();

        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus(OrderStatus.PENDING);

        existingUser.getOrders().add(newOrder);
        newOrder.setUser(existingUser);
        userRepository.save(existingUser);
        orderRepository.save(newOrder);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem itemDTO : usercart.getCartItems()) { // Lấy từng sản phẩm trong giỏ hàng củ ngdùng
            System.out.println("inside the loop");
            if (itemDTO.getCart().getCartId() == usercart.getCartID()) { // ??? khó hiểu

                OrderItem orderItem = new OrderItem();// creating New orderItem;

                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setProduct(itemDTO.getProduct());
                orderItem.setOrderId(newOrder.getOrderId());
                orderItems.add(orderItem);
                System.out.println("inside the loop and if");
            }
        }

        newOrder.setOrderItem(orderItems);
        newOrder.setTotalAmount(usercart.getTotalAmount());
        orderRepository.save(newOrder);


        usercart.setTotalAmount(usercart.getTotalAmount() - newOrder.getTotalAmount());
        cartItemRepository.removeAllProductFromCart(cartId);
        cartRepository.save(usercart);

        OrderResponse orderdata = new OrdersDTO();
        orderdata.setOrderId(newOrder.getOrderId());
        orderdata.setOrderAmount(newOrder.getTotalAmount());
        orderdata.setStatus("Pending");
        orderdata.setPaymentStatus("Pending");
        orderdata.setOrderDate("Currebt Date");
        return orderdata;

    }
    public Orders updateOrders(Integer ordersid,OrderResponse orderDTo)throws OrdersException;
    public void deleteOrders(Integer userId,Integer Orderid)throws OrdersException;



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

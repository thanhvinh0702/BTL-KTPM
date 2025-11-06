package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.Enum.PaymentMethod;
import com.ecommerce.paymentservice.Enum.PaymentStatus;
import com.ecommerce.paymentservice.client.OrderClient;
import com.ecommerce.paymentservice.dto.external.OrderResponse;
import com.ecommerce.paymentservice.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.exception.PaymentException;
import com.ecommerce.paymentservice.mapper.PaymentMapper;
import com.ecommerce.paymentservice.model.Payment;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final PaymentMapper paymentMapper;
    /**
     * Tạo đơn thanh toán
     */
    @Transactional
    public PaymentResponse makePayment(PaymentRequest request) throws PaymentException {
        // get order info from orderservice
        OrderResponse order = orderClient.getOrderById((request.getOrderId()));
        if (order == null) {
            throw new PaymentException("Order not found.");
        }

        // create payment record
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .userId(request.getUserId())
                .paymentAmount(order.getTotalAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(
                        request.getPaymentMethod() != null
                            ? request.getPaymentMethod()
                                : PaymentMethod.UPI
                )
                .paymentStatus(PaymentStatus.SUCCESSFUL)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        try {
            orderClient.updateOrderStatus(order.getId(), "PAID");
        } catch (Exception e) {
            throw new PaymentException("Payment saved, but failed to update order status: " + e.getMessage());
        }

        return paymentMapper.toPaymentResponse(savedPayment);
    }
}

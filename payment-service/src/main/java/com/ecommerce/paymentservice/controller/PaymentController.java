package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/makePayment")
    public ResponseEntity<PaymentResponse> makePayment(@RequestBody PaymentRequest request) {
        PaymentResponse payment = paymentService.makePayment(request);
        return ResponseEntity.ok(payment);
    }
}

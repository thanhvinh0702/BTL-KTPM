package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.request.UserCreditRequest;
import com.ecommerce.paymentservice.dto.response.UserCreditResponse;
import com.ecommerce.paymentservice.service.UserCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments/credits")
public class UserCreditController {

    private final UserCreditService userCreditService;

    @PutMapping
    public UserCreditResponse modifyCredit(@RequestBody @Validated UserCreditRequest userCreditRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userCreditService.modifyUserCredit(authentication.getName(), userCreditRequest.getAmount());
    }
}

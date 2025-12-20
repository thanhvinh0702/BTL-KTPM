package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.response.UserCreditResponse;
import com.ecommerce.paymentservice.exception.NotEnoughCreditException;
import com.ecommerce.paymentservice.mapper.UserCreditMapper;
import com.ecommerce.paymentservice.model.UserCredit;
import com.ecommerce.paymentservice.repository.UserCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreditService {

    private final UserCreditRepository userCreditRepository;
    private final UserCreditMapper userCreditMapper;

    public UserCreditResponse modifyUserCredit(String userId, Double amount) {
        UserCredit userCredit = userCreditRepository.findByUserId(userId).orElseGet(() ->
                userCreditRepository.save(UserCredit.builder()
                                .userId(userId)
                                .credit(0.0)
                                .build())
        );
        double newAmount = userCredit.getCredit() + amount;
        if (newAmount < 0) {
            throw new NotEnoughCreditException("Not enough credit to perform action.");
        }
        userCredit.setCredit(newAmount);
        userCreditRepository.save(userCredit);
        return userCreditMapper.toResponse(userCredit);
    }
}

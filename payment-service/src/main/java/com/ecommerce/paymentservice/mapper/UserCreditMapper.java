package com.ecommerce.paymentservice.mapper;

import com.ecommerce.paymentservice.dto.response.UserCreditResponse;
import com.ecommerce.paymentservice.model.UserCredit;
import org.springframework.stereotype.Component;

@Component
public class UserCreditMapper {

    public UserCreditResponse toResponse(UserCredit userCredit) {
        return UserCreditResponse.builder()
                .id(userCredit.getId())
                .userId(userCredit.getUserId())
                .credit(userCredit.getCredit())
                .build();
    }
}

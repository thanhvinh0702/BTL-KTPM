package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.model.UserCredit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCreditRepository extends JpaRepository<UserCredit, Long> {

    Optional<UserCredit> findByUserId(String userId);
}

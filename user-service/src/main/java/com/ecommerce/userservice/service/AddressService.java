package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.AddressRequest;
import com.ecommerce.userservice.dto.AddressResponse;
import com.ecommerce.userservice.mapper.AddressMapper;
import com.ecommerce.userservice.model.Address;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.AddressRepository;
import com.ecommerce.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    public AddressResponse createOrUpdateAddress(Long userId, AddressRequest addressRequest) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchElementException("No user found!"));
        Address address = Address.builder()
                .city(addressRequest.getCity())
                .state(addressRequest.getState())
                .flatNo(addressRequest.getFlatNo())
                .street(addressRequest.getStreet())
                .zipCode(addressRequest.getZipCode())
                .user(user)
                .build();
        user.setAddress(address);
        userRepository.save(user);
        return addressMapper.toResponse(address);
    }
}

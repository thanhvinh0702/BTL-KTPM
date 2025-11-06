package com.ecommerce.shippingservice.service;

import com.ecommerce.shippingservice.dto.ShippingRequest;
import com.ecommerce.shippingservice.model.ShippingDetails;
import com.ecommerce.shippingservice.repository.ShippingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {
    private final ShippingRepository shippingRepository;
    public ShippingService(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }
    public ShippingDetails addShippingDetails(Integer orderId, Integer shipperId,
                                              ShippingDetails shippingDetails) {
        if (shippingDetails == null)
            throw new NullArgumentException("ShippingDetails cannot be null");

        shippingRepository.save(shippingDetails);
        return shippingDetails;
    }

    public ShippingDetails updateShippingAddress(Integer shippingId, ShippingRequest shippingRequest){
        ShippingDetails existing = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new EntityNotFoundException("Shipping detail not found"));

        existing.setState(shippingRequest.getState());
        existing.setAddress(shippingRequest.getAddress());
        existing.setCity(shippingRequest.getCity());
        existing.setPostalCode(shippingRequest.getPostalCode());
        return shippingRepository.save(existing);
    }

    public void deleteShippingDetails(Integer shippingId) {
        ShippingDetails existing = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new EntityNotFoundException("Shipping detail not found"));

        shippingRepository.delete(existing);
    }



}

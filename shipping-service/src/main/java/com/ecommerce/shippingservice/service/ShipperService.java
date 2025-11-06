package com.ecommerce.shippingservice.service;

import com.ecommerce.shippingservice.model.Shipper;
import com.ecommerce.shippingservice.repository.ShipperRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class ShipperService {
    private final ShipperRepository shipperRepository;

    public  ShipperService(ShipperRepository shipperRepository) {
        this.shipperRepository = shipperRepository;
    }

    public Shipper getShipperById(@PathVariable Integer shipper_id) {
        return shipperRepository.findById(shipper_id)
                .orElseThrow(() -> new EntityNotFoundException("No shipper found with shipper id"));
    }

    public List<Shipper> getAllShippers() {
        return shipperRepository.findAll();
    }

    public Shipper addShipper(Shipper shipper) {
        return shipperRepository.save(shipper);
    }

    public void deleteShipperById(@PathVariable Integer shipper_id) {
        shipperRepository.deleteById(shipper_id);
    }



}

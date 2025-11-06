package com.ecommerce.shippingservice.controller;

import java.util.List;

import com.ecommerce.shippingservice.model.Shipper;
import com.ecommerce.shippingservice.service.ShipperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ecom/order-shippers")
public class ShipperController {

    private final ShipperService shipperService;

    public ShipperService getShipperService() {
        return shipperService;
    }

    @GetMapping("/{shipper_id}")
    public ResponseEntity<Shipper> getShipperById(@PathVariable Integer shipper_id) {
        Shipper shipper = shipperService.getShipperById(shipper_id);
        return ResponseEntity.ok(shipper);
    }

    @GetMapping
    public ResponseEntity<List<Shipper>> getAllShippers() {
        List<Shipper> shippers = shipperService.getAllShippers();
        return ResponseEntity.ok(shippers);
    }

    @PostMapping("/add")
    public ResponseEntity<Shipper> saveShipper(@Valid @RequestBody Shipper shipper) {
        Shipper savedShipper = shipperService.addShipper(shipper);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShipper);
    }

    @DeleteMapping("/{shipper_id}")
    public ResponseEntity<String> deleteShipperById(@PathVariable Integer shipper_id) {
        shipperService.deleteShipperById(shipper_id);
        return ResponseEntity.ok("Shipper with ID " + shipper_id + " successfully deleted.");
    }
}

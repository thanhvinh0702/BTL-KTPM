package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductRequest;
import com.ecommerce.productservice.dto.message.EventMessage;
import com.ecommerce.productservice.dto.message.OrderCreatedPayload;
import com.ecommerce.productservice.dto.message.OrderItem;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.model.SagaLog;
import com.ecommerce.productservice.model.SagaStatus;
import com.ecommerce.productservice.publisher.ProductEventPublisher;
import com.ecommerce.productservice.repository.ProductRepository;
import com.ecommerce.productservice.repository.SagaLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SagaLogRepository sagaLogRepository;
    private final SagaLogService sagaLogService;
    private final ProductEventPublisher productEventPublisher;
    private final ObjectMapper objectMapper;

    public Product findById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() ->
                new NoSuchElementException("Product with id " + productId + " not found!"));
    }

    public List<Product> findProducts(String category, String name) {
        if (category != null && name != null) {
            return productRepository.findByCategoryNameAndName(category, name);
        } else if (category != null) {
            return productRepository.findByCategoryName(category);
        } else if (name != null) {
            return productRepository.findByName(name);
        } else {
            return productRepository.findAll();
        }
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void idempotencyReserveProduct(EventMessage<OrderCreatedPayload> eventMessage, OrderCreatedPayload payload) {
        SagaLog sagaLog = sagaLogRepository.findById(eventMessage.getEventId()).orElseGet(() ->
        {
            try {
                return SagaLog.builder()
                        .sagaId(eventMessage.getEventId())
                        .status(SagaStatus.PENDING)
                        .payload(objectMapper.writeValueAsString(eventMessage.getPayload()))
                        .build();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        if (sagaLog.getStatus().equals(SagaStatus.COMPLETED) || sagaLog.getStatus().equals(SagaStatus.COMPENSATED)) {
            return;
        }
        try {
            reserveProduct(payload);
            sagaLog.setStatus(SagaStatus.COMPLETED);
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("product.success")
                    .occurredAt(Instant.now())
                    .source("product-service")
                    .payload(null)
                    .build();
            productEventPublisher.publishProductSuccessEvent(eventPublishedMessage);
            sagaLogService.save(sagaLog);
        }
        catch (Exception e) {
            sagaLog.setStatus(SagaStatus.COMPENSATED);
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("product.failed")
                    .occurredAt(Instant.now())
                    .source("product-service")
                    .payload(null)
                    .build();
            productEventPublisher.publishProductFailedEvent(eventPublishedMessage);
            sagaLogService.save(sagaLog);
            throw e;
        }
    }

    public void reserveProduct(OrderCreatedPayload payload) {
        for (OrderItem orderItem : payload.getOrderItems()) {
            Product product = productRepository.findByIdForUpdate(orderItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found " + orderItem.getProductId()));
            int newStock = product.getQuantity() - orderItem.getQuantity();
            if (newStock < 0) {
                throw new RuntimeException("Not enough stock for product " + orderItem.getProductId());
            }
            product.setQuantity(newStock);
            productRepository.save(product);
        }
    }

    @Transactional
    @PreAuthorize("@productSecurity.isOwner(#productId, #userId)")
    public Product updateProduct(Long productId, ProductRequest productRequest, Long userId) {
        Product existedProduct = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + productId + " not found!"));
        if (productRequest.getCategoryName() != null) {
            existedProduct.setCategoryName(productRequest.getCategoryName());
        }
        if (productRequest.getName() != null) {
            existedProduct.setName(productRequest.getName());
        }
        if (productRequest.getPrice() != null) {
            existedProduct.setPrice(productRequest.getPrice());
        }
        if (productRequest.getQuantity() != null) {
            existedProduct.setQuantity(productRequest.getQuantity());
        }
        if (productRequest.getImageUrl() != null) {
            existedProduct.setImageUrl(productRequest.getImageUrl());
        }
        if (productRequest.getDescription() != null) {
            existedProduct.setDescription(productRequest.getDescription());
        }
        return productRepository.save(existedProduct);
    }

    @PreAuthorize("@productSecurity.isOwner(#productId, #userId)")
    public Product deleteProduct(Long productId, Long userId) {
        Product existedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + productId + " not found!"));
        productRepository.deleteById(productId);
        return existedProduct;
    }
}

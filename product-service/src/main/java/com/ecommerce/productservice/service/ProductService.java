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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    public void idempotencyReserveProduct(EventMessage<OrderCreatedPayload> eventMessage) {
        sagaLogService.ensureSagaLogExists(eventMessage);
        int updated = sagaLogRepository.updateStatusIfMatches(
                eventMessage.getEventId(),
                SagaStatus.PENDING,
                SagaStatus.PROCESSING
        );
        if (updated == 0) {
            return;
        }
        try {
            reserveProduct(eventMessage.getPayload());
            sagaLogRepository.updateStatusIfMatches(
                    eventMessage.getEventId(),
                    SagaStatus.PROCESSING,
                    SagaStatus.COMPLETED
            );
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("product.success")
                    .occurredAt(Instant.now())
                    .source("product-service")
                    .payload(null)
                    .build();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    productEventPublisher.publishProductSuccessEvent(eventPublishedMessage);
                }
            });
        }
        catch (Exception e) {
            sagaLogService.failSaga(eventMessage.getEventId(), eventMessage.getCorrelationId());
            throw e;
        }
    }

    @Transactional
    public void idempotencyProductCompensation(String sagaId) {
        int nullUpdated = sagaLogRepository.insertIfNotExists(
                sagaId,
                SagaStatus.COMPENSATED.toString(),
                null
        );
        if (nullUpdated == 1) {
            this.publishEventAfterCommit(sagaId, null);
            return;
        }
        // Pending case -> no compensation
        int pendingUpdated = sagaLogRepository.updateStatusIfMatches(sagaId, SagaStatus.PENDING, SagaStatus.COMPENSATED);
        if (pendingUpdated == 1) {
            this.publishEventAfterCommit(sagaId, null);
            return;
        }
        // Processing case -> no compensation but throw error
        if (sagaLogRepository.existsBySagaIdAndStatus(sagaId, SagaStatus.PROCESSING)) {
            throw new RuntimeException("Saga is still processing");
        }
        // Completed case -> start compensation
        int completedUpdated = sagaLogRepository.updateStatusIfMatches(sagaId, SagaStatus.COMPLETED, SagaStatus.COMPENSATING);
        if (completedUpdated == 0) {
            return;
        }
        // Safely compensate here
        SagaLog sagaLog = sagaLogRepository.findById(sagaId).orElseThrow();
        if (sagaLog.getPayload() == null) {
            return;
        }
        OrderCreatedPayload payload = null;
        try {
            payload = objectMapper.readValue(sagaLog.getPayload(), OrderCreatedPayload.class);
            fillProduct(payload);
            sagaLogRepository.updateStatusIfMatches(sagaId, SagaStatus.COMPENSATING, SagaStatus.COMPENSATED);
            this.publishEventAfterCommit(sagaId, payload);
        } catch (Exception e) {
            sagaLogService.failCompensatedSaga(sagaId, payload.getOrderId().toString());
            throw new RuntimeException(e);
        }
    }

    private void publishEventAfterCommit(String sagaId, OrderCreatedPayload payload) {
        EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                .eventId(sagaId)
                .correlationId(payload != null ? payload.getOrderId().toString() : null)
                .eventType("product.compensation-success")
                .occurredAt(Instant.now())
                .source("product-service")
                .payload(null)
                .build();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                productEventPublisher.publishCompensatedProductSuccessEvent(eventPublishedMessage);
            }
        });
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

    public void fillProduct(OrderCreatedPayload payload) {
        for (OrderItem orderItem : payload.getOrderItems()) {
            Product product = productRepository.findByIdForUpdate(orderItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found " + orderItem.getProductId()));
            int newStock = product.getQuantity() + orderItem.getQuantity();
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

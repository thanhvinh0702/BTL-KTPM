package com.ecommerce.orderservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.order}")
    private String orderExchange;

    @Value("${rabbitmq.queue.payment-success}")
    private String paymentSuccessQueue;

    @Value("${rabbitmq.queue.payment-failed}")
    private String paymentFailedQueue;

    @Value("${rabbitmq.queue.payment-compensated-success}")
    private String paymentCompensatedSuccessQueue;

    @Value("${rabbitmq.queue.product-success}")
    private String productSuccessQueue;

    @Value("${rabbitmq.queue.product-failed}")
    private String productFailedQueue;

    @Value("${rabbitmq.queue.product-compensated-success}")
    private String productCompensatedSuccessQueue;

    @Value("${rabbitmq.queue.cart-success}")
    private String cartSuccessQueue;

    @Value("${rabbitmq.queue.cart-failed}")
    private String cartFailedQueue;

    @Value("${rabbitmq.queue.cart-compensated-success}")
    private String cartCompensatedSuccessQueue;

    @Value("${rabbitmq.queue.shipping-success}")
    private String shippingSuccessQueue;

    @Value("${rabbitmq.queue.shipping-failed}")
    private String shippingFailedQueue;

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(orderExchange);
    }

    @Bean
    public Queue paymentSuccessQueue() {
        return new Queue(paymentSuccessQueue, true);
    }

    @Bean
    public Queue paymentFailedQueue() {
        return new Queue(paymentFailedQueue, true);
    }

    @Bean
    public Queue paymentCompensatedSuccessQueue() {
        return new Queue(paymentCompensatedSuccessQueue, true);
    }

    @Bean
    public Queue productSuccessQueue() {
        return new Queue(productSuccessQueue, true);
    }

    @Bean
    public Queue productFailedQueue() {
        return new Queue(productFailedQueue, true);
    }

    @Bean
    public Queue productCompensatedSuccessQueue() {
        return new Queue(productCompensatedSuccessQueue, true);
    }

    @Bean
    public Queue cartSuccessQueue() {
        return new Queue(cartSuccessQueue, true);
    }

    @Bean
    public Queue cartFailedQueue() {
        return new Queue(cartFailedQueue, true);
    }

    @Bean
    public Queue cartCompensatedSuccessQueue() {
        return new Queue(cartCompensatedSuccessQueue, true);
    }

    @Bean
    public Queue shippingSuccessQueue() {
        return new Queue(shippingSuccessQueue, true);
    }

    @Bean
    public Queue shippingFailedQueue() {
        return new Queue(shippingFailedQueue, true);
    }

    @Bean
    public Binding bindingPaymentSuccess() {
        return BindingBuilder.bind(paymentSuccessQueue())
                .to(orderExchange())
                .with("payment.success");
    }

    @Bean
    public Binding bindingPaymentFailed() {
        return BindingBuilder.bind(paymentFailedQueue())
                .to(orderExchange())
                .with("payment.failed");
    }

    @Bean
    public Binding bindingPaymentCompensatedSuccess() {
        return BindingBuilder.bind(paymentCompensatedSuccessQueue())
                .to(orderExchange())
                .with("payment.compensated-success");
    }

    @Bean
    public Binding bindingProductSuccess() {
        return BindingBuilder.bind(productSuccessQueue())
                .to(orderExchange())
                .with("product.success");
    }

    @Bean
    public Binding bindingProductFailed() {
        return BindingBuilder.bind(productFailedQueue())
                .to(orderExchange())
                .with("product.failed");
    }

    @Bean
    public Binding bindingProductCompensatedSuccess() {
        return BindingBuilder.bind(productCompensatedSuccessQueue())
                .to(orderExchange())
                .with("product.compensate-success");
    }

    @Bean
    public Binding bindingCartSuccess() {
        return BindingBuilder.bind(cartSuccessQueue())
                .to(orderExchange())
                .with("cart.success");
    }

    @Bean
    public Binding bindingCartFailed() {
        return BindingBuilder.bind(cartFailedQueue())
                .to(orderExchange())
                .with("cart.failed");
    }

    @Bean
    public Binding bindingCartCompensatedSuccess() {
        return BindingBuilder.bind(cartCompensatedSuccessQueue())
                .to(orderExchange())
                .with("cart.compensated-success");
    }

    @Bean
    public Binding bindingShippingSuccess() {
        return BindingBuilder.bind(shippingSuccessQueue())
                .to(orderExchange())
                .with("shipping.success");
    }

    @Bean
    public Binding bindingShippingFailed() {
        return BindingBuilder.bind(shippingFailedQueue())
                .to(orderExchange())
                .with("shipping.failed");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        return rabbitTemplate;
    }
}

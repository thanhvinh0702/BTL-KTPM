package com.ecommerce.cartservice.event.config;



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
    // ORDER <-> CART events
    @Value("${rabbitmq.exchange.order}")
    private String orderExchangeName;

    @Value("${rabbitmq.queue.cart.order-created}")
    private String cartOrderCreatedQueue;

    @Value("${rabbitmq.routing-key.cart.order-created}")
    private String cartOrderCreatedRoutingKey;

    // Internal cart queue
    @Value("${rabbitmq.queue.cart.internal}")
    private String cartInternalQueue;

    // CART -> ORDER events
    @Value("${rabbitmq.exchange.cart}")
    private String cartExchangeName;

    // PRODUCT -> CART events
    @Value("${rabbitmq.exchange.product}")
    private String productExchangeName;


    @Value("${rabbitmq.queue.product.updated}")
    private String productUpdatedQueueName;

    @Value("${rabbitmq.routing-key.product.updated}")
    private String productUpdatedKey;

    @Value("${rabbitmq.queue.product.out-of-stock}")
    private String outOfStockQueue;

    @Value("${rabbitmq.routing-key.product.out-of-stock}")
    private String outOfStockKey;

    @Value("${rabbitmq.queue.product.back-in-stock}")
    private String backInStockQueue;

    @Value("${rabbitmq.routing-key.product.back-in-stock}")
    private String backInStockKey;

    @Value("${rabbitmq.queue.product.low-stock}")
    private String lowStockQueue;

    @Value("${rabbitmq.routing-key.product.low-stock}")
    private String lowStockKey;


    // EXCHANGES
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(orderExchangeName);
    }

    @Bean
    public TopicExchange cartExchange() {
        return new TopicExchange(cartExchangeName);
    }

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(productExchangeName);
    }

    // QUEUES (consumer)
    @Bean
    public Queue cartOrderCreatedQueue() {
        return new Queue(cartOrderCreatedQueue, true);
    }

    @Bean
    public Queue cartInternalQueue() {
        return new Queue(cartInternalQueue, true);
    }

    @Bean
    public Queue productUpdatedQueue() {
        return new Queue(productUpdatedQueueName, true);
    }

    @Bean
    public Queue productOutOfStockQueue() {
        return new Queue(outOfStockQueue, true);
    }

    @Bean
    public Queue productBackInStockQueue() {
        return new Queue(backInStockQueue, true);
    }

    @Bean
    public Queue productLowStockQueue() {
        return new Queue(lowStockQueue, true);
    }

    // BINDING
    @Bean
    public Binding cartOrderCreatedBinding() {
        return BindingBuilder.bind(cartOrderCreatedQueue())
                .to(orderExchange())
                .with(cartOrderCreatedRoutingKey);
    }

    @Bean
    public Binding bindingProductUpdate() {
        return BindingBuilder.bind(productUpdatedQueue())
                .to(productExchange())
                .with(productUpdatedKey);
    }

    @Bean
    public Binding bindingOutOfStock() {
        return BindingBuilder.bind(productOutOfStockQueue())
                .to(productExchange())
                .with(outOfStockKey);
    }

    @Bean
    public Binding bindingBackInStock() {
        return BindingBuilder.bind(productBackInStockQueue())
                .to(productExchange())
                .with(backInStockKey);
    }

    @Bean
    public Binding bindingLowStock() {
        return BindingBuilder.bind(productLowStockQueue())
                .to(productExchange())
                .with(lowStockKey);
    }

    // MESSAGE CONVERTER
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RABBIT TEMPLATE
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }






}

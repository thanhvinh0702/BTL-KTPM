package com.ecommerce.productservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange orderExchange(
            @Value("${rabbitmq.exchange.order}") String exchange
    ) {
        return new TopicExchange(exchange);
    }


    @Bean
    public Queue productOrderCreatedQueue(@Value("${rabbitmq.queue.product.order-created}") String queue) {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Queue productOrderCompensatedQueue(@Value("${rabbitmq.queue.product.order-compensate}") String queue) {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding productOrderCreatedBinding(
            Queue productOrderCreatedQueue,
            TopicExchange orderExchange,
            @Value("${rabbitmq.routing-key.product.order-created}") String routingKey
    ) {
        return BindingBuilder
                .bind(productOrderCreatedQueue)
                .to(orderExchange)
                .with(routingKey);
    }

    @Bean
    public Binding productOrderCompensatedBinding(
            Queue productOrderCompensatedQueue,
            TopicExchange orderExchange,
            @Value("${rabbitmq.routing-key.product.order-compensate}") String routingKey
    ) {
        return BindingBuilder
                .bind(productOrderCompensatedQueue)
                .to(orderExchange)
                .with(routingKey);
    }


    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}

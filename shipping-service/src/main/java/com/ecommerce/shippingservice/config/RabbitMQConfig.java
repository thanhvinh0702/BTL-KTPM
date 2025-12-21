package com.ecommerce.shippingservice.config;

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
    public Queue shippingOrderCreatedQueue(@Value("${rabbitmq.queue.shipping.order-confirmed}") String queue) {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding shippingOrderCreatedBinding(
            Queue shippingOrderCreatedQueue,
            TopicExchange orderExchange,
            @Value("${rabbitmq.routing-key.shipping.order-confirmed}") String routingKey
    ) {
        return BindingBuilder
                .bind(shippingOrderCreatedQueue)
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


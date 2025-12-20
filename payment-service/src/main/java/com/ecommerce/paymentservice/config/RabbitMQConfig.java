package com.ecommerce.paymentservice.config;

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
    public Queue paymentOrderCreatedQueue(@Value("${rabbitmq.queue.payment.order-created}") String queue) {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding paymentOrderCreatedBinding(
            Queue paymentOrderCreatedQueue,
            TopicExchange orderExchange,
            @Value("${rabbitmq.routing-key.payment.order-created}") String routingKey
    ) {
        return BindingBuilder
                .bind(paymentOrderCreatedQueue)
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

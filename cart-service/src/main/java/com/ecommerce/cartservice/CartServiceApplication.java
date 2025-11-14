package com.ecommerce.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@EnableFeignClients(basePackages = "com.ecommerce.cartservice.client")
public class CartServiceApplication {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(CartServiceApplication.class, args);

        System.out.println("Mongo URI: " + ctx.getEnvironment().getProperty("spring.data.mongodb.uri"));
        System.out.println("Mongo DB: " + ctx.getEnvironment().getProperty("spring.data.mongodb.database"));
    }

}

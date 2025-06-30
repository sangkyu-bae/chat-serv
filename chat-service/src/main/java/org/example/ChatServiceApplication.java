package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableWebFlux
@SpringBootApplication
//@EnableDiscoveryClient
public class ChatServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }
}
package org.example.modules.websoket.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setSendTimeLimit(15 * 1000)
                   .setSendBufferSizeLimit(512 * 1024)
                   .setMessageSizeLimit(128 * 1024);
    }

//    @Bean
//    public TaskScheduler messageBrokerTaskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(1);
//        scheduler.setThreadNamePrefix("wss-message-broker-");
//        scheduler.initialize();
//        return scheduler;
//    }
}

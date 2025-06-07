package org.example.module.client;

import org.example.domain.ChatMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "chat-service")
public interface ChatServerClient {

    @PostMapping("/send/chat")
    void sendMessage(@RequestBody ChatMessage request);
}

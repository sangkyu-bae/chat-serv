package org.example.modules.manager;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ServerManager {


    @Value("${server.port}")
    private String port;


    @Getter
    private String runtimeUrl;

    @PostConstruct
    public void init() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            this.runtimeUrl = "http://" + ip + ":";
            log.info("Server Runtime URL = {}", runtimeUrl);
        } catch (UnknownHostException e) {
            log.error("Failed to determine host IP address.", e);
        }
    }
    public String getPort(){
        return port;
    }

    public String getRuntimeUrl() {
        return runtimeUrl;
    }
}

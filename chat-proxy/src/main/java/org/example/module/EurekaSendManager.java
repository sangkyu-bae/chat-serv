package org.example.module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EurekaSendManager {
    private final DiscoveryClient discoveryClient;

    public List<String> getRegisteredServices() {
        List<String> serviceList =  discoveryClient.getServices();
        log.info(serviceList.toString());
        return serviceList;
    }

    public List<String> getServiceInstances(String serviceId) {
        return discoveryClient.getInstances(serviceId)
                .stream()
                .map(instance -> String.format("%s:%d", 
                    instance.getHost(), 
                    instance.getPort()))
                .toList();
    }
}

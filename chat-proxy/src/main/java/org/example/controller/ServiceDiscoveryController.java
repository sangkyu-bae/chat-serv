package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.module.EurekaSendManager;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/descovry")
@Tag(name = "Eureka 관련 API", description = "Eureka 관련 API")
public class ServiceDiscoveryController {
    private final EurekaSendManager eurekaSendManager;



    @Operation(summary = "erueka에 등록된 서버 조회", description = "erueka에 등록된 서버 조회를 조회합니다.")
    @GetMapping("/services")
    public List<String> getRegisteredServices() {
        return eurekaSendManager.getRegisteredServices();
    }

    @GetMapping("/services/{serviceId}")
    public List<String> getServiceInstances(@PathVariable String serviceId) {
        return eurekaSendManager.getServiceInstances(serviceId);
    }
} 
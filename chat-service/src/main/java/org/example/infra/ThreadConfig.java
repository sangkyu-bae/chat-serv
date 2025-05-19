package org.example.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadConfig {
    
    /**
     * 채팅 관련 비동기 작업을 위한 가상 스레드 기반 스케줄러
     */
    @Bean
    public Scheduler chatScheduler() {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        return Schedulers.fromExecutor(executorService);
    }
    
    /**
     * 기존 코드와의 호환성을 위한 ExecutorService
     */
    @Bean
    public ExecutorService chatWorkerThreadPool() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}

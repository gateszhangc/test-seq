package com.dap.sequence.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author yzp
 * @date 2024/7/17 9:03
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 配置的核心线程数量
     */
    @Value("${dap.sequence.product.threadPool.coreThreadSize:}")
    private Integer coreThreadSize;

    /**
     * 配置的最大线程数量
     */
    @Value("${dap.sequence.product.threadPool.maxThreadSize:}")
    private Integer maxThreadSize;

    /**
     * 配置的线程队列数量
     */
    @Value("${dap.sequence.product.threadPool.threadQueueSize:}")
    private Integer threadQueueSize;

    /**
     * 配置的线程空闲时间
     */
    @Value("${dap.sequence.product.threadPool.threadKeepAliveSeconds:60}")
    private Integer threadKeepAliveSeconds;


    @Bean("seqTaskExecutor")
    @Lazy
    public ThreadPoolTaskExecutor seqTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("seq-product-");
        // 优雅关闭线程池：等待任务执行完毕后关机
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // 获取服务器的cpu个数
        int cpuCount = Runtime.getRuntime().availableProcessors();
        // 核心线程数
        threadPoolTaskExecutor.setCorePoolSize(coreThreadSize == null ? cpuCount * 2 : coreThreadSize);
        // 最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(maxThreadSize == null ? cpuCount * 8 : maxThreadSize);
        // 线程缓冲任务队列数量
        threadPoolTaskExecutor.setQueueCapacity(threadQueueSize == null ? cpuCount * 16 : threadQueueSize);
        // 线程队列拒绝策略：线程缓冲任务队列满后交由调用者线程执行
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲时间
        threadPoolTaskExecutor.setKeepAliveSeconds(threadKeepAliveSeconds);
        return threadPoolTaskExecutor;
    }
}

package com.gls.athena.sdk.amap.support;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 高德地图API监控拦截器
 * <p>
 * 该拦截器用于收集高德地图API的调用统计信息，包括：
 * 1. 请求总数
 * 2. 请求频率
 * 3. API使用情况
 *
 * @author george
 */
@Slf4j
public class AmapMetricsInterceptor implements RequestInterceptor {

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong lastRequestTime = new AtomicLong(0);

    @Override
    public void apply(RequestTemplate template) {
        // 记录请求统计
        long requestCount = totalRequests.incrementAndGet();
        long currentTime = System.currentTimeMillis();
        lastRequestTime.set(currentTime);

        // 记录API调用信息
        String apiPath = template.path();
        log.debug("Amap API call - Path: {}, Total Requests: {}", apiPath, requestCount);

        // 可以在这里添加更多监控逻辑，如发送到监控系统
        recordApiCall(apiPath, currentTime);
    }

    /**
     * 记录API调用信息
     */
    private void recordApiCall(String apiPath, long timestamp) {
        // 这里可以集成具体的监控系统，如Micrometer、Prometheus等
        // 暂时使用日志记录
        if (log.isDebugEnabled()) {
            log.debug("API调用记录 - 接口: {}, 时间: {}, 总请求数: {}",
                    apiPath, timestamp, totalRequests.get());
        }
    }

    /**
     * 获取总请求数
     */
    public long getTotalRequests() {
        return totalRequests.get();
    }

    /**
     * 获取最后请求时间
     */
    public long getLastRequestTime() {
        return lastRequestTime.get();
    }
}

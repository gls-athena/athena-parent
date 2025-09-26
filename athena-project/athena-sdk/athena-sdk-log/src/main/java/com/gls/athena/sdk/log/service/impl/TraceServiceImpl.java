package com.gls.athena.sdk.log.service.impl;

import com.gls.athena.sdk.log.service.ITraceService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

/**
 * 跟踪服务实现
 * 职责：专门负责分布式跟踪相关功能
 *
 * @author george
 */
@Slf4j
@Service
@ConditionalOnClass(Tracer.class)
public class TraceServiceImpl implements ITraceService {

    @Resource
    private Tracer tracer;

    /**
     * 获取当前跟踪ID
     * 从当前线程的跟踪上下文中提取traceId，用于分布式链路追踪
     *
     * @return String 当前跟踪ID，如果无法获取则返回null
     */
    @Override
    public String getCurrentTraceId() {
        try {
            // 获取当前活跃的跟踪跨度
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                // 从跨度上下文中提取跟踪ID
                return currentSpan.context().traceId();
            }
        } catch (Exception e) {
            log.debug("获取跟踪ID失败：{}", e.getMessage());
        }
        return null;
    }
}


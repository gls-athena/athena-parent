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

    @Override
    public String getCurrentTraceId() {
        try {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                return currentSpan.context().traceId();
            }
        } catch (Exception e) {
            log.debug("获取跟踪ID失败：{}", e.getMessage());
        }
        return null;
    }
}

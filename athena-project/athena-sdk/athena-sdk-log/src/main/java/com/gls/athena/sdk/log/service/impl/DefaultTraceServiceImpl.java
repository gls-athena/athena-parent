package com.gls.athena.sdk.log.service.impl;

import com.gls.athena.sdk.log.service.ITraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 默认跟踪服务实现
 * 职责：在没有分布式跟踪组件时提供默认实现
 *
 * @author george
 */
@Slf4j
@Service
@ConditionalOnMissingBean(ITraceService.class)
public class DefaultTraceServiceImpl implements ITraceService {

    @Override
    public String getCurrentTraceId() {
        // 生成简单的UUID作为跟踪ID
        return UUID.randomUUID().toString().replace("-", "");
    }
}

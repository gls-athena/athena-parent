package com.gls.athena.sdk.log.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.sdk.log.domain.MethodLogDto;
import com.gls.athena.sdk.log.service.IMethodLogPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 方法日志发布器实现
 * 职责：专门负责异步发布方法日志事件
 *
 * @author george
 */
@Slf4j
@Service
public class MethodLogPublisherImpl implements IMethodLogPublisher {

    @Override
    @Async("logTaskExecutor")
    public void publishLog(MethodLogDto methodLogDto) {
        try {
            log.debug("方法执行完毕，发布日志事件：{}", methodLogDto.getMethodName());
            SpringUtil.publishEvent(methodLogDto);
        } catch (Exception e) {
            // 日志发布失败不应影响主业务流程
            log.error("方法日志发布失败，方法：{}.{}, 错误：{}",
                    methodLogDto.getClassName(), methodLogDto.getMethodName(), e.getMessage());
        }
    }
}

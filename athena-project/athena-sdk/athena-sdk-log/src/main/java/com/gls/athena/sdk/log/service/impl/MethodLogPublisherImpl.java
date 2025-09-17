package com.gls.athena.sdk.log.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.sdk.log.domain.MethodLogDto;
import com.gls.athena.sdk.log.service.IMethodLogPublisher;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 异步发布方法日志事件
     * 通过Spring事件机制将方法执行日志发布出去，使用独立的线程池执行以避免影响主业务流程
     *
     * @param methodLogDto 方法日志数据传输对象，包含方法执行的相关信息
     */
    @Override
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


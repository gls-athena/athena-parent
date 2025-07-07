package com.gls.athena.sdk.log.config;

import cn.hutool.json.JSONUtil;
import com.gls.athena.sdk.log.method.IMethodEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 日志基础配置
 * 职责：提供默认的方法事件监听器实现
 *
 * @author george
 */
@Slf4j
@AutoConfiguration
public class LogConfig {

    /**
     * 默认方法事件监听器
     * 当没有其他实现时，提供基于日志输出的默认实现
     *
     * @return IMethodEventListener 默认方法事件监听器
     */
    @Bean
    @ConditionalOnMissingBean(IMethodEventListener.class)
    public IMethodEventListener defaultMethodEventListener() {
        return event -> log.info("MethodEvent: {}", JSONUtil.toJsonStr(event));
    }
}

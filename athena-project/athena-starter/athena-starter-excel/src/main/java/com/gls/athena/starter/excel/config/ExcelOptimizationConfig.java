package com.gls.athena.starter.excel.config;

import com.gls.athena.starter.excel.chain.ExcelProcessorChain;
import com.gls.athena.starter.excel.factory.ReadListenerFactory;
import com.gls.athena.starter.excel.listener.OptimizedReadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Excel优化配置类
 * <p>
 * 提供基于设计模式优化后的Excel组件配置
 *
 * @author george
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "athena.excel.optimization.enabled", havingValue = "true", matchIfMissing = false)
public class ExcelOptimizationConfig {

    /**
     * 配置默认的Excel处理器链
     */
    @Bean
    public ExcelProcessorChain defaultExcelProcessorChain() {
        ExcelProcessorChain chain = ExcelProcessorChain.buildDefaultChain();
        log.info("创建默认Excel处理器链");
        return chain;
    }

    /**
     * 配置优化后的读取监听器
     */
    @Bean
    public OptimizedReadListener<?> optimizedReadListener(ExcelProcessorChain processorChain) {
        OptimizedReadListener<?> listener = new OptimizedReadListener<>(processorChain);
        log.info("创建优化后的Excel读取监听器");
        return listener;
    }

    /**
     * 初始化监听器工厂缓存
     */
    @Bean
    public ReadListenerFactory readListenerFactory() {
        log.info("初始化读取监听器工厂");
        return new ReadListenerFactory();
    }
}

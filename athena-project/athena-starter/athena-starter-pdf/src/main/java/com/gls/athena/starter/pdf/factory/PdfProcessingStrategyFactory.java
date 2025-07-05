package com.gls.athena.starter.pdf.factory;

import com.gls.athena.starter.pdf.strategy.PdfProcessingStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PDF处理策略工厂
 * 应用工厂模式管理不同的PDF处理策略
 * 优化了策略获取性能和大小写处理
 *
 * @author george
 */
@Component
@RequiredArgsConstructor
public class PdfProcessingStrategyFactory {

    private final List<PdfProcessingStrategy> strategies;

    /**
     * 策略映射缓存，使用ConcurrentHashMap保证线程安全
     * key使用大写字母，避免大小写问题
     */
    private final Map<String, PdfProcessingStrategy> strategyCache = new ConcurrentHashMap<>();

    /**
     * 初始化策略映射缓存
     */
    @PostConstruct
    public void initStrategyCache() {
        strategyCache.putAll(
                strategies.stream()
                        .collect(Collectors.toMap(
                                strategy -> strategy.getSupportedType().toUpperCase(), // 统一转换为大写
                                Function.identity(),
                                (existing, replacement) -> existing
                        ))
        );
    }

    /**
     * 获取PDF处理策略
     * 支持大小写不敏感的模板类型匹配
     *
     * @param templateType 模板类型（大小写不敏感）
     * @return PDF处理策略
     * @throws IllegalArgumentException 不支持的模板类型
     */
    public PdfProcessingStrategy getStrategy(String templateType) {
        if (templateType == null || templateType.trim().isEmpty()) {
            throw new IllegalArgumentException("模板类型不能为空");
        }

        // 转换为大写进行匹配，解决大小写问题
        String normalizedType = templateType.trim().toUpperCase();
        PdfProcessingStrategy strategy = strategyCache.get(normalizedType);

        if (strategy == null) {
            throw new IllegalArgumentException("不支持的模板类型: " + templateType +
                    ", 支持的类型: " + String.join(", ", strategyCache.keySet()));
        }

        return strategy;
    }

    /**
     * 获取所有支持的模板类型
     *
     * @return 支持的模板类型集合
     */
    public java.util.Set<String> getSupportedTypes() {
        return strategyCache.keySet();
    }
}

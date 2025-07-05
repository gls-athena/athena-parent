package com.gls.athena.starter.pdf.factory;

import com.gls.athena.starter.pdf.strategy.PdfProcessingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PDF处理策略工厂
 * 应用工厂模式管理不同的PDF处理策略
 *
 * @author george
 */
@Component
@RequiredArgsConstructor
public class PdfProcessingStrategyFactory {

    private final List<PdfProcessingStrategy> strategies;

    /**
     * 获取PDF处理策略
     *
     * @param templateType 模板类型
     * @return PDF处理策略
     * @throws IllegalArgumentException 不支持的模板类型
     */
    public PdfProcessingStrategy getStrategy(String templateType) {
        return getStrategyMap().computeIfAbsent(templateType, type -> {
            throw new IllegalArgumentException("不支持的模板类型: " + type);
        });
    }

    /**
     * 获取策略映射表（懒加载）
     */
    private Map<String, PdfProcessingStrategy> getStrategyMap() {
        return strategies.stream()
                .collect(Collectors.toMap(
                        PdfProcessingStrategy::getSupportedType,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
    }
}

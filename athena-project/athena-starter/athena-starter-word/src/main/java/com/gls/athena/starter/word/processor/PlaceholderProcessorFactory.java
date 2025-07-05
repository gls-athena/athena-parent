package com.gls.athena.starter.word.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 占位符处理器工厂
 * 使用工厂模式和责任链模式管理不同的占位符处理器
 *
 * @author athena
 */
@Slf4j
@Component
public class PlaceholderProcessorFactory {

    private final List<PlaceholderProcessor> processors;

    @Autowired
    public PlaceholderProcessorFactory(List<PlaceholderProcessor> processors) {
        // 按优先级排序，优先级数值越小越靠前
        this.processors = processors.stream()
                .sorted(Comparator.comparingInt(PlaceholderProcessor::getPriority))
                .toList();
    }

    /**
     * 处理占位符
     *
     * @param placeholder 占位符内容（不包含 ${ } 包装）
     * @param data        数据上下文
     * @return 处理后的文本
     */
    public String process(String placeholder, Map<String, Object> data) {
        for (PlaceholderProcessor processor : processors) {
            if (processor.supports(placeholder)) {
                try {
                    return processor.process(placeholder, data);
                } catch (Exception e) {
                    log.warn("Processor {} failed to process placeholder: {}",
                            processor.getClass().getSimpleName(), placeholder, e);
                    // 继续尝试下一个处理器
                }
            }
        }

        // 如果没有处理器能处理，返回原始占位符
        log.debug("No processor found for placeholder: {}", placeholder);
        return "${" + placeholder + "}";
    }
}

package com.gls.athena.starter.word.support;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordTemplateType;
import com.gls.athena.starter.word.support.template.TemplateProcessor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 模板处理器管理器
 *
 * @author lizy19
 */
@Slf4j
@Component
public class TemplateProcessorManager {

    @Resource
    private List<TemplateProcessor> processors;

    /**
     * 处理Word模板，自动根据模板类型选择合适的处理器
     *
     * @param data         包含填充模板所需数据的映射
     * @param outputStream 用于输出生成的文件的流
     * @param wordResponse 提供与Word相关的响应处理
     */
    public void handleTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {
        getProcessor(wordResponse.templateType()).processTemplate(data, outputStream, wordResponse);
    }

    /**
     * 根据模板类型获取对应的模板处理器
     *
     * @param templateType 模板类型
     * @return 对应的模板处理器
     * @throws IllegalArgumentException 如果没有找到匹配的处理器
     */
    private TemplateProcessor getProcessor(WordTemplateType templateType) {
        return processors.stream()
                .filter(processor -> processor.supports(templateType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的模板类型: " + templateType));
    }
}

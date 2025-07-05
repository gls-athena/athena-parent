package com.gls.athena.starter.word.generator.impl;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordDocumentGenerator;
import com.gls.athena.starter.word.service.TemplateProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 基于模板的Word文档生成器
 * 重构版本：使用设计模式优化架构，提供更好的可维护性和扩展性
 *
 * 使用的设计模式：
 * - 策略模式：不同类型的占位符处理器
 * - 工厂模式：处理器工厂管理
 * - 模板方法模式：文档处理流程
 * - 责任链模式：文档元素处理链
 * - 建造者模式：复杂上下文对象构建
 *
 * @author athena
 */
@Slf4j
@Component
public class TemplateWordDocumentGenerator implements WordDocumentGenerator {

    @Autowired
    private TemplateProcessingService templateProcessingService;

    @Override
    public XWPFDocument generate(Object data, WordResponse wordResponse) {
        validateInput(data, wordResponse);

        try {
            return templateProcessingService.processTemplate(wordResponse.template(), data);
        } catch (Exception e) {
            log.error("Failed to generate Word document from template: {}", wordResponse.template(), e);
            throw new RuntimeException("Failed to generate Word document from template", e);
        }
    }

    @Override
    public boolean supports(Class<?> dataClass) {
        // 支持Map类型和普通Java对象
        return Map.class.isAssignableFrom(dataClass) || !dataClass.isPrimitive();
    }

    /**
     * 验证输入参数
     *
     * @param data         数据对象
     * @param wordResponse Word响应注解
     */
    private void validateInput(Object data, WordResponse wordResponse) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        if (wordResponse == null) {
            throw new IllegalArgumentException("WordResponse annotation is required");
        }

        String templatePath = wordResponse.template();
        if (!StringUtils.hasText(templatePath)) {
            throw new IllegalArgumentException("Template path is required for template-based Word document");
        }
    }
}

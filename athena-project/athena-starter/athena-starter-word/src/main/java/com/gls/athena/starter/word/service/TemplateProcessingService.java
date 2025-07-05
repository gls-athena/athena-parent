package com.gls.athena.starter.word.service;

import com.gls.athena.starter.word.context.TemplateContext;
import com.gls.athena.starter.word.converter.DataConverter;
import com.gls.athena.starter.word.filler.DocumentFiller;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 模板处理服务
 * 使用模板方法模式定义文档处理流程
 *
 * @author athena
 */
@Slf4j
@Service
public class TemplateProcessingService {

    @Autowired
    private DataConverter dataConverter;

    @Autowired
    private List<DocumentFiller> documentFillers;

    /**
     * 处理模板文档
     *
     * @param templatePath 模板路径
     * @param data         数据对象
     * @return 处理后的文档
     */
    public XWPFDocument processTemplate(String templatePath, Object data) {
        // 构建处理上下文
        TemplateContext context = TemplateContext.builder()
                .templatePath(templatePath)
                .data(dataConverter.convertAndEnrich(data))
                .build();

        return processTemplate(context);
    }

    /**
     * 处理模板文档（使用上下文）
     *
     * @param context 模板处理上下文
     * @return 处理后的文档
     */
    public XWPFDocument processTemplate(TemplateContext context) {
        validateContext(context);

        XWPFDocument document = loadTemplate(context.getTemplatePath());
        context.setDocument(document);

        fillDocument(document, context.getData());

        return document;
    }

    /**
     * 验证上下文
     *
     * @param context 模板上下文
     */
    private void validateContext(TemplateContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Template context cannot be null");
        }

        if (!StringUtils.hasText(context.getTemplatePath())) {
            throw new IllegalArgumentException("Template path is required");
        }

        if (context.getData() == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
    }

    /**
     * 加载模板文档
     *
     * @param templatePath 模板路径
     * @return 文档对象
     */
    private XWPFDocument loadTemplate(String templatePath) {
        try {
            Resource templateResource = new ClassPathResource(templatePath);
            if (!templateResource.exists()) {
                throw new IllegalArgumentException("Template file not found: " + templatePath);
            }

            XWPFDocument document = new XWPFDocument(templateResource.getInputStream());
            log.info("Successfully loaded template: {}", templatePath);
            return document;

        } catch (IOException e) {
            log.error("Failed to load template: {}", templatePath, e);
            throw new RuntimeException("Failed to load template: " + templatePath, e);
        }
    }

    /**
     * 填充文档内容
     *
     * @param document 文档对象
     * @param data     数据Map
     */
    private void fillDocument(XWPFDocument document, Map<String, Object> data) {
        try {
            for (DocumentFiller filler : documentFillers) {
                filler.fill(document, data);
            }
            log.info("Document filled successfully with {} data entries", data.size());

        } catch (Exception e) {
            log.error("Failed to fill document", e);
            throw new RuntimeException("Failed to fill document", e);
        }
    }
}

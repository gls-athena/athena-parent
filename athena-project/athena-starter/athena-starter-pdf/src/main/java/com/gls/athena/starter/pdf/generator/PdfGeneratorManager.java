package com.gls.athena.starter.pdf.generator;

import com.gls.athena.starter.pdf.config.TemplateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.OutputStream;

/**
 * 简化的PDF生成器管理服务
 *
 * @author athena
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorManager {

    private static final String HTML_EXTENSION = ".html";
    private static final String HTM_EXTENSION = ".htm";

    private final PdfGenerator generator;

    /**
     * 生成PDF文档
     */
    public void generate(Object data, String template, TemplateType templateType, OutputStream outputStream) throws Exception {
        TemplateType actualType = determineTemplateType(template, templateType);
        log.debug("使用统一生成器处理模板类型: {}", actualType);
        generator.generate(data, template, actualType, outputStream);
    }

    /**
     * 确定模板类型
     */
    private TemplateType determineTemplateType(String template, TemplateType configuredType) {
        if (configuredType != TemplateType.AUTO) {
            return configuredType;
        }

        // 自动检测模板类型
        if (!StringUtils.hasText(template)) {
            return TemplateType.DATA;
        }

        String lowerTemplate = template.toLowerCase();
        if (lowerTemplate.endsWith(HTML_EXTENSION) || lowerTemplate.endsWith(HTM_EXTENSION)) {
            return TemplateType.HTML;
        }

        return TemplateType.DATA;
    }
}

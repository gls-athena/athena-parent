package com.gls.athena.starter.pdf.generator;

import com.gls.athena.starter.pdf.config.TemplateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.OutputStream;
import java.util.List;

/**
 * PDF生成器管理服务
 *
 * @author athena
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorManager {

    private final List<PdfGenerator> generators;

    /**
     * 生成PDF文档
     *
     * @param data         数据对象
     * @param template     模板路径
     * @param templateType 模板类型
     * @param outputStream 输出流
     * @throws Exception 生成异常
     */
    public void generate(Object data, String template, TemplateType templateType, OutputStream outputStream) throws Exception {
        TemplateType actualType = determineTemplateType(template, templateType);
        PdfGenerator generator = selectGenerator(actualType);
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
        if (lowerTemplate.endsWith(".html") || lowerTemplate.endsWith(".htm")) {
            return TemplateType.HTML;
        }

        return TemplateType.DATA;
    }

    /**
     * 选择合适的生成器
     *
     * @param templateType 模板类型
     * @return PDF生成器
     */
    private PdfGenerator selectGenerator(TemplateType templateType) {
        for (PdfGenerator generator : generators) {
            if (generator.supports(templateType)) {
                log.debug("选择生成器: {} 处理模板类型: {}", generator.getClass().getSimpleName(), templateType);
                return generator;
            }
        }

        throw new RuntimeException("没有找到合适的PDF生成器处理模板类型: " + templateType);
    }
}

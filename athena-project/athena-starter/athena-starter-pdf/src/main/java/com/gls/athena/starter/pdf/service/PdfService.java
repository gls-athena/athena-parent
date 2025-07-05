package com.gls.athena.starter.pdf.service;

import com.gls.athena.starter.pdf.config.TemplateType;
import com.gls.athena.starter.pdf.factory.PdfProcessingStrategyFactory;
import com.gls.athena.starter.pdf.strategy.PdfProcessingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * PDF服务门面
 * 应用门面模式，为客户端提供简化的PDF操作接口
 *
 * @author george
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdfService {

    private final PdfProcessingStrategyFactory strategyFactory;

    /**
     * 生成PDF字节数组
     *
     * @param data         数据
     * @param template     模板名称
     * @param templateType 模板类型
     * @return PDF字节数组
     */
    public byte[] generatePdf(Map<String, Object> data, String template, TemplateType templateType) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            generatePdf(data, template, templateType, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("生成PDF失败: {}", e.getMessage(), e);
            throw new RuntimeException("PDF生成失败", e);
        }
    }

    /**
     * 生成PDF到输出流
     *
     * @param data         数据
     * @param template     模板名称
     * @param templateType 模板类型
     * @param outputStream 输出流
     */
    public void generatePdf(Map<String, Object> data, String template,
                            TemplateType templateType, OutputStream outputStream) {
        try {
            PdfProcessingStrategy strategy = strategyFactory.getStrategy(templateType.getCode());
            strategy.process(data, template, outputStream);
            log.info("PDF生成成功: template={}, type={}", template, templateType);
        } catch (Exception e) {
            log.error("生成PDF失败: template={}, type={}, error={}", template, templateType, e.getMessage(), e);
            throw new RuntimeException("PDF生成失败", e);
        }
    }
}

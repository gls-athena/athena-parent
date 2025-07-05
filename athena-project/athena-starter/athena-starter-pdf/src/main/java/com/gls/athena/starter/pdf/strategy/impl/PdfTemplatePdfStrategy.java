package com.gls.athena.starter.pdf.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.starter.pdf.exception.PdfProcessingException;
import com.gls.athena.starter.pdf.strategy.PdfProcessingStrategy;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * PDF模板填充处理策略
 * 合并了PDF模板处理的所有功能，提高内聚性
 *
 * @author george
 */
@Slf4j
@Component
public class PdfTemplatePdfStrategy implements PdfProcessingStrategy {

    @Override
    public void process(Map<String, Object> data, String template, OutputStream outputStream) throws Exception {
        try {
            // 加载PDF模板
            try (InputStream templateStream = new ClassPathResource(template).getInputStream()) {
                log.debug("加载PDF模板成功: template={}", template);

                // 直接在策略内部处理PDF模板填充
                fillPdfTemplate(templateStream, data, outputStream);
                log.info("PDF模板填充成功: template={}", template);
            }
        } catch (Exception e) {
            log.error("PDF模板处理失败: template={}, error={}", template, e.getMessage(), e);
            throw new PdfProcessingException("PDF模板处理失败: " + template, e);
        }
    }

    @Override
    public String getSupportedType() {
        return "PDF";
    }

    /**
     * 填充PDF模板表单字段并输出结果
     * 原PdfTemplateUtil的功能合并到此策略中
     */
    private void fillPdfTemplate(InputStream inputStream, Map<String, Object> data,
                                 OutputStream outputStream) throws IOException {
        PdfReader reader = new PdfReader(inputStream);
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        AcroFields fields = stamper.getAcroFields();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = StrUtil.toString(entry.getValue());
            try {
                fields.setField(key, value);
            } catch (IOException e) {
                log.warn("填充字段失败: {}", key, e);
            }
        }

        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
    }
}

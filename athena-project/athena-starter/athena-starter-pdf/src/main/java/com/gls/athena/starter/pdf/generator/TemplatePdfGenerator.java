package com.gls.athena.starter.pdf.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Map;

/**
 * 模板PDF生成器实现
 * <p>
 * 该类负责根据注解配置和数据对象生成PDF文档。
 * </p>
 *
 * @author athena
 */
@Slf4j
@Component
public class TemplatePdfGenerator implements PdfGenerator {
    @Resource
    private PdfProperties pdfProperties;

    /**
     * 生成PDF文档
     *
     * @param data         数据对象
     * @param pdfResponse  PDF响应注解，包含模板路径等信息
     * @param outputStream 输出流，用于写入生成的PDF文档
     * @throws Exception 生成PDF时可能抛出的异常
     */
    @Override
    public void generate(Object data, PdfResponse pdfResponse, OutputStream outputStream) throws Exception {
        // 根据传入的数据对象、注解中的模板路径，生成PDF并写入输出流
        generateHtmlPdf(data, pdfResponse.template(), outputStream);
    }

    /**
     * 判断是否支持当前注解配置
     *
     * @param pdfResponse PDF响应注解
     * @return 是否支持该配置
     */
    @Override
    public boolean supports(PdfResponse pdfResponse) {
        // 判断注解中是否配置了模板路径，且生成器类型为PdfGenerator
        return StrUtil.isNotBlank(pdfResponse.template())
                && pdfResponse.generator().equals(PdfGenerator.class);
    }

    /**
     * 根据模板和数据生成HTML并转为PDF输出
     *
     * @param data         数据对象
     * @param template     模板路径
     * @param outputStream 输出流
     */
    private void generateHtmlPdf(Object data, String template, OutputStream outputStream) {
        try {
            // 渲染HTML模板
            String templatePath = template.startsWith("classpath:") ?
                    template.substring("classpath:".length()) : template;

            String html = TemplateUtil.createEngine(pdfProperties.getTemplateConfig())
                    .getTemplate(templatePath)
                    .render(convertToMap(data));

            // HTML转PDF
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();

            log.info("HTML模板PDF生成成功: {}", template);
        } catch (Exception e) {
            throw new RuntimeException("HTML模板PDF生成失败: " + template, e);
        }
    }

    /**
     * 将数据对象转换为Map格式，便于模板渲染
     *
     * @param data 数据对象
     * @return Map格式数据
     */
    private Map<?, ?> convertToMap(Object data) {
        if (data == null) {
            throw new IllegalArgumentException("数据对象不能为空");
        }
        if (data instanceof Map<?, ?> map) {
            return map;
        }
        return BeanUtil.beanToMap(data);
    }
}

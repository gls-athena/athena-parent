package com.gls.athena.starter.pdf.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfProperties;
import com.gls.athena.starter.pdf.util.PdfUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * 模板PDF生成器实现
 * <p>
 * 该类负责根据注解配置和数据对象生成PDF文档。
 * </p>
 *
 * @author george
 */
@Slf4j
@Component
public class TemplatePdfGenerator implements PdfGenerator {

    @Resource
    private PdfProperties pdfProperties;

    /**
     * 使用模板生成PDF文档
     * <p>
     * 本方法通过解析数据对象和PDF响应注解中的模板信息，将数据渲染到模板中，并将结果输出为PDF文档。
     * </p>
     *
     * @param data         数据对象，将被渲染到模板中
     * @param pdfResponse  PDF响应注解，包含模板信息
     * @param outputStream PDF文档的输出流
     * @throws Exception 如果生成PDF过程中发生错误
     */
    @Override
    public void generate(Object data, PdfResponse pdfResponse, OutputStream outputStream) throws Exception {
        String template = pdfResponse.template();
        try {
            // 渲染HTML模板
            TemplateConfig templateConfig = new TemplateConfig(pdfProperties.getCharset(), pdfProperties.getTemplatePath(), pdfProperties.getResourceMode());

            String html = TemplateUtil.createEngine(templateConfig)
                    .getTemplate(template)
                    .render(BeanUtil.beanToMap(data));

            // HTML转PDF
            ITextRenderer renderer = new ITextRenderer();
            // 设置字体解析器，添加所需的字体文件
            PdfUtil.addClasspathFonts(renderer, pdfProperties.getFontPath());
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
     * 判断是否支持指定的PDF响应注解
     * <p>
     * 本方法检查PDF响应注解是否指定了模板，并且生成器是否为PdfGenerator类型。
     * </p>
     *
     * @param pdfResponse PDF响应注解
     * @return 如果支持，则返回true；否则返回false
     */
    @Override
    public boolean supports(PdfResponse pdfResponse) {
        return StrUtil.isNotBlank(pdfResponse.template())
                && pdfResponse.generator().equals(PdfGenerator.class);
    }

}

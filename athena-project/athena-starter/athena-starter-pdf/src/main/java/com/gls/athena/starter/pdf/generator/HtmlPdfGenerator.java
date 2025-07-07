package com.gls.athena.starter.pdf.generator;

import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.starter.pdf.config.PdfProperties;
import com.gls.athena.starter.pdf.config.TemplateType;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.pdf.ITextFontResolver;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * 基于HTML模板的PDF生成器
 *
 * @author athena
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlPdfGenerator implements PdfGenerator {

    private final PdfProperties pdfProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void generate(Object data, String template, TemplateType templateType, OutputStream outputStream) throws Exception {
        try {
            // 渲染HTML模板
            TemplateConfig config = new TemplateConfig("", TemplateConfig.ResourceMode.CLASSPATH);
            if (template.startsWith("classpath:")) {
                // 去掉classpath前缀
                template = template.substring("classpath:".length());
            }
            String html = TemplateUtil.createEngine(config).getTemplate(template).render(convertToMap(data));
            log.debug("渲染HTML模板成功: template={}", template);

            // 直接在策略内部处理HTML到PDF的转换
            generatePdfFromHtml(html, outputStream);
            log.info("HTML模板PDF生成成功: template={}", template);

        } catch (Exception e) {
            log.error("HTML模板PDF生成失败: template={}, error={}", template, e.getMessage(), e);
            throw new RuntimeException("HTML模板PDF生成失败: " + template, e);
        }
    }

    @Override
    public boolean supports(TemplateType templateType) {
        return templateType == TemplateType.HTML ||
                (templateType == TemplateType.AUTO && StringUtils.hasText(""));
    }

    /**
     * 从HTML生成PDF
     */
    private void generatePdfFromHtml(String htmlContent, OutputStream outputStream) throws DocumentException, IOException {
        ITextRenderer renderer = new ITextRenderer();
        addClasspathFonts(renderer);
        // 设置HTML内容
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        // 生成PDF
        renderer.createPDF(outputStream);
        renderer.finishPDF();
    }

    private void addClasspathFonts(ITextRenderer renderer) throws IOException {

        String path = pdfProperties.getDefaultFontPath();
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources(path + "*.*");

        ITextFontResolver fontResolver = renderer.getFontResolver();
        for (Resource font : resources) {
            try {
                log.debug("加载字体: {}", font.getFile().getAbsolutePath());
                fontResolver.addFont(font.getFile().getAbsolutePath(),
                        BaseFont.IDENTITY_H,
                        BaseFont.NOT_EMBEDDED);
            } catch (IOException e) {
                log.warn("无法加载字体: {}", font.getFile().getAbsolutePath(), e);
            }
        }
    }

    /**
     * 将数据对象转换为Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object data) {
        if (data == null) {
            return Map.of();
        }

        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }

        // 使用Jackson转换为Map
        return objectMapper.convertValue(data, Map.class);
    }
}

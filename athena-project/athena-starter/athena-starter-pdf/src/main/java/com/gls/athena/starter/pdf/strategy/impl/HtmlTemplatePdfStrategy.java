package com.gls.athena.starter.pdf.strategy.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateUtil;
import com.gls.athena.starter.pdf.config.PdfProperties;
import com.gls.athena.starter.pdf.exception.PdfProcessingException;
import com.gls.athena.starter.pdf.strategy.PdfProcessingStrategy;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.pdf.ITextFontResolver;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * HTML模板PDF处理策略
 * 合并了HTML到PDF转换的所有功能，提高内聚性
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlTemplatePdfStrategy implements PdfProcessingStrategy {

    private final PdfProperties pdfProperties;

    @Override
    public void process(Map<String, Object> data, String template, OutputStream outputStream) throws Exception {
        try {
            // 渲染HTML模板
            String html = TemplateUtil.createEngine(pdfProperties.getTemplateConfig())
                    .getTemplate(template)
                    .render(data);
            log.debug("渲染HTML模板成功: template={}", template);

            // 直接在策略内部处理HTML到PDF的转换
            writeHtmlToPdf(html, pdfProperties.getFontPath(), outputStream);
            log.info("HTML模板PDF生成成功: template={}", template);

        } catch (Exception e) {
            log.error("HTML模板PDF生成失败: template={}, error={}", template, e.getMessage(), e);
            throw new PdfProcessingException("HTML模板PDF生成失败: " + template, e);
        }
    }

    @Override
    public String getSupportedType() {
        return "HTML";
    }

    /**
     * 将HTML内容转换为PDF格式并输出
     * 原HtmlToPdfUtil的功能合并到此策略中
     */
    private void writeHtmlToPdf(String html, String fontPath, OutputStream outputStream) throws IOException {
        ITextRenderer renderer = new ITextRenderer();
        addClasspathFonts(renderer, fontPath);
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);
    }

    /**
     * 为ITextRenderer添加类路径下的字体资源
     */
    private void addClasspathFonts(ITextRenderer renderer, String fontPath) throws IOException {
        String path = normalizeClasspathPath(fontPath);
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:" + path + "*.*");

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
     * 标准化classpath路径格式
     */
    private String normalizeClasspathPath(String path) {
        if (StrUtil.isBlank(path)) {
            return "";
        }
        path = path.trim().replaceAll("^/+|/+$", "");
        if (!path.isEmpty()) {
            path += "/";
        }
        return path;
    }
}

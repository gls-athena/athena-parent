package com.gls.athena.starter.pdf.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfProperties;
import com.lowagie.text.pdf.BaseFont;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.pdf.ITextFontResolver;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
            TemplateConfig templateConfig = new TemplateConfig(pdfProperties.getCharset(), pdfProperties.getTemplatePath(), pdfProperties.getResourceMode());

            String html = TemplateUtil.createEngine(templateConfig)
                    .getTemplate(template)
                    .render(convertToMap(data));

            // HTML转PDF
            ITextRenderer renderer = new ITextRenderer();
            // 设置字体解析器，添加所需的字体文件
            addClasspathFonts(renderer, pdfProperties.getFontPath());
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

    /**
     * 为ITextRenderer添加类路径下的字体资源
     * <p>
     * 该方法从类路径的/fonts目录加载字体文件，并将其添加到渲染器的字体解析器中。
     * 字体将使用IDENTITY_H编码（支持Unicode字符）且不嵌入PDF文档。
     *
     * @param renderer 需要添加字体的ITextRenderer实例
     * @param fontPath 字体路径，默认为"fonts"
     * @throws IOException 如果无法读取字体目录或字体文件时抛出
     */
    private void addClasspathFonts(ITextRenderer renderer, String fontPath) throws IOException {

        // 标准化路径处理
        String path = normalizeClasspathPath(fontPath);

        org.springframework.core.io.Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:" + path + "*.*");

        ITextFontResolver fontResolver = renderer.getFontResolver();
        for (org.springframework.core.io.Resource font : resources) {
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

    private String normalizeClasspathPath(String path) {
        if (StrUtil.isBlank(path)) {
            return "";
        }

        // 去除前后空白和斜杠
        path = path.trim().replaceAll("^/+|/+$", "");

        // 如果路径不为空，则在末尾添加一个斜杠
        if (!path.isEmpty()) {
            path += "/";
        }

        return path;
    }
}

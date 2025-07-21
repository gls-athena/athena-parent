package com.gls.athena.starter.pdf.util;

import cn.hutool.core.util.StrUtil;
import com.lowagie.text.pdf.BaseFont;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.pdf.ITextFontResolver;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * PDF工具类，提供静态方法来处理PDF相关的操作
 *
 * @author george
 */
@Slf4j
@UtilityClass
public class PdfUtil {

    /**
     * 从类路径中加载字体并添加到渲染器
     *
     * @param renderer PDF渲染器，用于解析和渲染PDF文档
     * @param fontPath 字体路径，指定在类路径中的位置
     * @throws IOException 当资源加载失败时抛出此异常
     */
    public void addClasspathFonts(ITextRenderer renderer, String fontPath) throws IOException {
        // 标准化路径处理
        String path = normalizeClasspathPath(fontPath);

        // 加载指定路径下的所有字体资源
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:" + path + "*.*");

        ITextFontResolver fontResolver = renderer.getFontResolver();
        for (Resource font : resources) {
            try {
                // 记录加载字体文件的日志
                log.info("加载字体文件: {}", font.getFilename());
                // 添加字体到渲染器，支持Unicode字符，但不嵌入字体到PDF
                fontResolver.addFont(font.getFile().getAbsolutePath(),
                        BaseFont.IDENTITY_H,
                        BaseFont.NOT_EMBEDDED);
            } catch (IOException e) {
                // 记录加载字体文件失败的日志
                log.error("加载字体文件失败: {}", font.getFilename(), e);
            }
        }
    }

    /**
     * 标准化类路径，包括去除前后空白和斜杠，并在末尾添加一个斜杠
     *
     * @param path 原始路径字符串
     * @return 标准化后的路径字符串
     */
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

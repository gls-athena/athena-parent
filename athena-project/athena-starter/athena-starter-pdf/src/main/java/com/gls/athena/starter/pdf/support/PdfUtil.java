package com.gls.athena.starter.pdf.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.pdf.ITextFontResolver;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author george
 */
@Slf4j
@UtilityClass
public class PdfUtil {

    /**
     * 获取用于输出PDF文件的OutputStream
     *
     * @param webRequest NativeWebRequest对象，用于获取HttpServletResponse
     * @param fileName   要输出的文件名（自动补全.pdf扩展名）
     * @return 响应输出流，用于写入PDF文件内容
     * @throws IOException              如果获取输出流失败
     * @throws IllegalArgumentException 如果文件名为空
     * @throws IllegalStateException    如果无法获取HttpServletResponse对象
     */
    public OutputStream getOutputStream(NativeWebRequest webRequest, String fileName) throws IOException {
        // 参数校验：确保文件名不为空
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 从webRequest中获取HttpServletResponse对象并进行非空校验
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalStateException("无法获取HttpServletResponse对象");
        }

        // 设置响应头：字符编码、内容类型为PDF
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/pdf");

        // 处理文件名：URL编码并确保有.pdf扩展名
        String encodedFileName = URLUtil.encode(fileName, StandardCharsets.UTF_8);
        String finalFileName = encodedFileName.endsWith(".pdf") ? encodedFileName : encodedFileName + ".pdf";

        // 设置Content-Disposition头（附件下载）和CORS相关头
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + finalFileName);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return response.getOutputStream();
    }

    /**
     * 将HTML内容转换为PDF格式并输出
     * 此方法使用ITextRenderer库将给定的HTML字符串渲染成PDF格式，并将结果写入指定的输出流
     * 主要包括以下几个步骤：
     * 1. 实例化ITextRenderer对象
     * 2. 使用提供的HTML字符串设置文档内容
     * 3. 布局文档
     * 4. 创建PDF并写入输出流
     *
     * @param html         要转换为PDF的HTML字符串
     * @param fontPath     字体路径
     * @param outputStream 用于输出生成的PDF文件的流
     */
    public void writeHtmlToPdf(String html, String fontPath, OutputStream outputStream) throws IOException {
        // 实例化ITextRenderer对象，用于HTML到PDF的转换
        ITextRenderer renderer = new ITextRenderer();
        // 设置字体解析器，添加所需的字体文件
        addClasspathFonts(renderer, fontPath);

        // 设置文档内容为提供的HTML字符串
        renderer.setDocumentFromString(html);

        // 执行文档布局
        renderer.layout();

        // 创建PDF并将其写入指定的输出流
        renderer.createPDF(outputStream);
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
     * 1. 去除前后多余斜杠
     * 2. 确保路径末尾有且只有一个斜杠
     *
     * @param path 原始路径
     * @return 标准化后的路径
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

    /**
     * 填充PDF模板表单字段并输出结果
     *
     * @param inputStream  包含PDF模板的输入流，必须是一个可读的PDF文件
     * @param data         包含字段名和对应值的映射，将用于填充PDF表单字段
     * @param outputStream 用于输出填充后PDF文档的输出流
     * @throws IOException 如果读取输入流或写入输出流时发生I/O错误
     */
    public void fillPdfTemplate(InputStream inputStream, Map<String, Object> data,
                                OutputStream outputStream) throws IOException {
        // 初始化PDF文档处理器
        PdfReader reader = new PdfReader(inputStream);
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        // 获取PDF表单字段并填充数据
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
        // 扁平化表单字段并关闭资源
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
    }

}

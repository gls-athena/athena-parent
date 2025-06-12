package com.gls.athena.starter.pdf.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author george
 */
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
     * @param outputStream 用于输出生成的PDF文件的流
     */
    public void writeHtmlToPdf(String html, OutputStream outputStream) throws IOException {
        // 实例化ITextRenderer对象，用于HTML到PDF的转换
        ITextRenderer renderer = new ITextRenderer();
        // 设置字体解析器，添加所需的字体文件
        addClasspathFonts(renderer);

        // 设置文档内容为提供的HTML字符串
        renderer.setDocumentFromString(html);

        // 执行文档布局
        renderer.layout();

        // 创建PDF并将其写入指定的输出流
        renderer.createPDF(outputStream);
    }

    private void addClasspathFonts(ITextRenderer renderer) throws IOException {
        // 添加字体文件
        ClassPathResource simsun = new ClassPathResource("/fonts/simsun.ttc");
        renderer.getFontResolver().addFont(simsun.getPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        ClassPathResource msyh = new ClassPathResource("/fonts/msyh.ttc");
        renderer.getFontResolver().addFont(msyh.getPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    }

    /**
     * 填充PDF模板表单字段并输出结果
     *
     * @param inputStream  包含PDF模板的输入流，必须是一个可读的PDF文件
     * @param data         包含字段名和对应值的映射，将用于填充PDF表单字段
     * @param outputStream 用于输出填充后PDF文档的输出流
     * @throws IOException 如果读取输入流或写入输出流时发生I/O错误
     */
    public void fillPdfTemplate(InputStream inputStream, Map<String, Object> data, OutputStream outputStream) throws IOException {
        // 初始化PDF文档处理器
        PdfReader reader = new PdfReader(inputStream);
        PdfStamper stamper = new PdfStamper(reader, outputStream);

        // 获取PDF表单字段并填充数据
        AcroFields fields = stamper.getAcroFields();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            fields.setField(entry.getKey(), entry.getValue().toString());
        }

        // 扁平化表单字段并关闭资源
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
    }

}

package com.gls.athena.starter.pdf.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
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
    public void writeHtmlToPdf(String html, OutputStream outputStream) {
        // 实例化ITextRenderer对象，用于HTML到PDF的转换
        ITextRenderer renderer = new ITextRenderer();

        // 设置文档内容为提供的HTML字符串
        renderer.setDocumentFromString(html);

        // 执行文档布局
        renderer.layout();

        // 创建PDF并将其写入指定的输出流
        renderer.createPDF(outputStream);
    }

    @SneakyThrows
    public void fillPdfTemplate(InputStream inputStream, Map<String, Object> data, OutputStream outputStream) {
        PdfReader reader = new PdfReader(inputStream);
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        AcroFields fields = stamper.getAcroFields();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            fields.setField(entry.getKey(), entry.getValue().toString());
        }
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
    }
}

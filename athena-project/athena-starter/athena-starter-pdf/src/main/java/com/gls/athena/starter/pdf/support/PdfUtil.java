package com.gls.athena.starter.pdf.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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

}

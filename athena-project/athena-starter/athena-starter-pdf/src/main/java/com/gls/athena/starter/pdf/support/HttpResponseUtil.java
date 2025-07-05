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
 * HTTP响应工具类
 * 专门处理HTTP响应相关操作，遵循单一职责原则
 *
 * @author george
 */
@UtilityClass
public class HttpResponseUtil {

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
        validateFileName(fileName);

        HttpServletResponse response = extractResponse(webRequest);
        setupResponseHeaders(response, fileName);

        return response.getOutputStream();
    }

    /**
     * 验证文件名
     */
    private void validateFileName(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
    }

    /**
     * 提取HttpServletResponse
     */
    private HttpServletResponse extractResponse(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalStateException("无法获取HttpServletResponse对象");
        }
        return response;
    }

    /**
     * 设置响应头
     */
    private void setupResponseHeaders(HttpServletResponse response, String fileName) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/pdf");

        String encodedFileName = URLUtil.encode(fileName, StandardCharsets.UTF_8);
        String finalFileName = encodedFileName.endsWith(".pdf") ? encodedFileName : encodedFileName + ".pdf";

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + finalFileName);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
    }
}

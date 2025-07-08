package com.gls.athena.starter.jasper.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.jasper.config.ReportType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Jasper HTTP响应处理服务 - 专门负责HTTP响应的配置和处理
 *
 * @author george
 */
@Slf4j
@Service
public class JasperResponseService {

    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";

    /**
     * 配置HTTP响应并获取输出流
     *
     * @param webRequest     Web请求对象
     * @param jasperResponse 响应配置
     * @return 配置好的输出流
     * @throws IOException 处理异常
     */
    public OutputStream configureResponseAndGetOutputStream(NativeWebRequest webRequest, JasperResponse jasperResponse) throws IOException {
        validateParameters(jasperResponse);

        HttpServletResponse response = getHttpServletResponse(webRequest);
        configureResponseHeaders(response, jasperResponse);

        return response.getOutputStream();
    }

    /**
     * 验证参数有效性
     */
    private void validateParameters(JasperResponse jasperResponse) {
        if (StrUtil.isBlank(jasperResponse.filename())) {
            throw new IllegalArgumentException("文件名不能为空");
        }
    }

    /**
     * 获取HttpServletResponse对象
     */
    private HttpServletResponse getHttpServletResponse(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalStateException("无法获取HttpServletResponse对象");
        }
        return response;
    }

    /**
     * 配置响应头
     */
    private void configureResponseHeaders(HttpServletResponse response, JasperResponse jasperResponse) {
        ReportType reportType = jasperResponse.reportType();

        // 设置基本响应头
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(reportType.getContentType());

        // 设置文件下载相关头
        String fileName = sanitizeAndEncodeFileName(jasperResponse.filename(), reportType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, fileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        log.debug("响应头配置完成: filename={}, contentType={}", fileName, reportType.getContentType());
    }

    /**
     * 安全编码文件名
     */
    private String sanitizeAndEncodeFileName(String originalFileName, ReportType reportType) {
        // 移除不安全字符
        String sanitizedFileName = originalFileName.replaceAll("[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]", "_");

        // URL编码并添加扩展名
        String encodedFileName = URLUtil.encode(sanitizedFileName, StandardCharsets.UTF_8);
        return encodedFileName + reportType.getExtension();
    }
}

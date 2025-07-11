package com.gls.athena.starter.pdf.handler;

import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfProperties;
import com.gls.athena.starter.pdf.generator.PdfGenerator;
import com.gls.athena.starter.pdf.generator.PdfGeneratorManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * PDF响应处理器（优化版）
 *
 * @author athena
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdfResponseHandler implements HandlerMethodReturnValueHandler {

    private static final String PDF_EXTENSION = ".pdf";
    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String ROOT_PATH = "/";

    private final PdfGeneratorManager generatorManager;
    private final PdfProperties pdfProperties;
    private final ApplicationContext applicationContext;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(PdfResponse.class) ||
                returnType.getContainingClass().isAnnotationPresent(PdfResponse.class);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  MethodParameter returnType,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        // 标记请求已处理，不再进行视图渲染
        mavContainer.setRequestHandled(true);

        // 获取注解配置
        PdfResponse pdfResponse = getPdfResponseAnnotation(returnType);
        if (pdfResponse == null) {
            log.warn("未找到@PdfResponse注解");
            return;
        }

        // 获取HttpServletResponse
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            log.error("无法获取HttpServletResponse");
            return;
        }

        try {
            // 生成文件名
            String fileName = generateFileName(pdfResponse.fileName());

            // 设置响应头
            setResponseHeaders(response, fileName, pdfResponse.inline());

            // 获取模板路径
            String templatePath = resolveTemplatePath(pdfResponse.template());

            // 生成PDF文档
            try (OutputStream outputStream = response.getOutputStream()) {
                // 检查是否指定了自定义生成器
                if (pdfResponse.generator() != PdfGenerator.class) {
                    // 使用自定义生成器
                    PdfGenerator customGenerator = getCustomGenerator(pdfResponse.generator());
                    customGenerator.generate(returnValue, templatePath, pdfResponse.templateType(), outputStream);
                } else {
                    // 使用默认的生成器管理器
                    generatorManager.generate(returnValue, templatePath, pdfResponse.templateType(), outputStream);
                }
                outputStream.flush();
            }

            log.info("PDF文档生成成功, 文件名: {}, 模板: {}, 类型: {}",
                    fileName, templatePath, pdfResponse.templateType());

        } catch (Exception e) {
            log.error("PDF文档生成失败", e);
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"error\":\"PDF文档生成失败\"}");
        }
    }

    /**
     * 获取PdfResponse注解
     */
    private PdfResponse getPdfResponseAnnotation(MethodParameter returnType) {
        PdfResponse annotation = returnType.getMethodAnnotation(PdfResponse.class);
        if (annotation == null) {
            annotation = returnType.getContainingClass().getAnnotation(PdfResponse.class);
        }
        return annotation;
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String configuredFileName) {
        if (StringUtils.hasText(configuredFileName)) {
            // 如果没有扩展名，添加.pdf
            if (!configuredFileName.toLowerCase().endsWith(PDF_EXTENSION)) {
                configuredFileName += PDF_EXTENSION;
            }
            return configuredFileName;
        }

        // 使用默认文件名格式：prefix_yyyyMMdd_HHmmss.pdf
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return pdfProperties.getFilePrefix() + "_" + timestamp + PDF_EXTENSION;
    }

    /**
     * 解析模板路径
     */
    private String resolveTemplatePath(String template) {
        if (!StringUtils.hasText(template)) {
            return null;
        }

        // 如果是相对路径，添加默认模板路径前缀
        if (!template.startsWith(CLASSPATH_PREFIX) && !template.startsWith(ROOT_PATH)) {
            return pdfProperties.getTemplatePath() + template;
        }

        return template;
    }

    /**
     * 设置响应头
     */
    private void setResponseHeaders(HttpServletResponse response, String fileName, boolean inline) {
        response.setContentType("application/pdf");
        response.setCharacterEncoding("UTF-8");

        // 设置文件下载或内联显示头
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        String disposition = inline ? "inline" : "attachment";
        response.setHeader("Content-Disposition",
                disposition + "; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    /**
     * 获取自定义生成器实例
     */
    private PdfGenerator getCustomGenerator(Class<? extends PdfGenerator> generatorClass) {
        try {
            // 先尝试从Spring容器中获取
            return applicationContext.getBean(generatorClass);
        } catch (Exception e) {
            // 如果容器中没有，则创建新实例
            try {
                return generatorClass.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                log.error("无法创建自定义生成器实例: {}", generatorClass.getName(), ex);
                throw new RuntimeException("无法创建自定义生成器: " + generatorClass.getName(), ex);
            }
        }
    }
}

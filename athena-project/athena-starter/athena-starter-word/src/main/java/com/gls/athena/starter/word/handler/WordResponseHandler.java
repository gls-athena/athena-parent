package com.gls.athena.starter.word.handler;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordProperties;
import com.gls.athena.starter.word.generator.WordGeneratorManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
 * Word响应处理器
 *
 * @author athena
 */
@Slf4j
@Component
public class WordResponseHandler implements HandlerMethodReturnValueHandler {

    private final WordGeneratorManager generatorManager;
    private final WordProperties wordProperties;

    public WordResponseHandler(WordGeneratorManager generatorManager, WordProperties wordProperties) {
        this.generatorManager = generatorManager;
        this.wordProperties = wordProperties;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(WordResponse.class) ||
                returnType.getContainingClass().isAnnotationPresent(WordResponse.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        // 标记请求已处理，不再进行视图渲染
        mavContainer.setRequestHandled(true);

        // 获取注解配置
        WordResponse wordResponse = getWordResponseAnnotation(returnType);
        if (wordResponse == null) {
            log.warn("未找到@WordResponse注解");
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
            String fileName = generateFileName(wordResponse.fileName());

            // 设置响应头
            setResponseHeaders(response, fileName);

            // 获取模板路径
            String templatePath = resolveTemplatePath(wordResponse.template());

            // 生成Word文档
            try (OutputStream outputStream = response.getOutputStream()) {
                generatorManager.generate(returnValue, templatePath, outputStream);
                outputStream.flush();
            }

            log.info("Word文档生成成功, 文件名: {}, 模板: {}", fileName, templatePath);

        } catch (Exception e) {
            log.error("Word文档生成失败", e);
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"error\":\"Word文档生成失败\"}");
        }
    }

    /**
     * 获取WordResponse注解
     */
    private WordResponse getWordResponseAnnotation(MethodParameter returnType) {
        WordResponse annotation = returnType.getMethodAnnotation(WordResponse.class);
        if (annotation == null) {
            annotation = returnType.getContainingClass().getAnnotation(WordResponse.class);
        }
        return annotation;
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String configuredFileName) {
        if (StringUtils.hasText(configuredFileName)) {
            // 如果没有扩展名，添加.docx
            if (!configuredFileName.toLowerCase().endsWith(".docx") &&
                    !configuredFileName.toLowerCase().endsWith(".doc")) {
                configuredFileName += ".docx";
            }
            return configuredFileName;
        }

        // 使用默认文件名格式：prefix_yyyyMMdd_HHmmss.docx
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return wordProperties.getDefaultFilePrefix() + "_" + timestamp + ".docx";
    }

    /**
     * 解析模板路径
     */
    private String resolveTemplatePath(String template) {
        if (!StringUtils.hasText(template)) {
            return null;
        }

        // 如果是相对路径，添加默认模板路径前缀
        if (!template.startsWith("classpath:") && !template.startsWith("/")) {
            return wordProperties.getDefaultTemplatePath() + template;
        }

        return template;
    }

    /**
     * 设置响应头
     */
    private void setResponseHeaders(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setCharacterEncoding("UTF-8");

        // 设置文件下载头
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }
}

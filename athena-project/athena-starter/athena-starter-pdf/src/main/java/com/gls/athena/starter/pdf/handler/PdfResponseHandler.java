package com.gls.athena.starter.pdf.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.generator.PdfGeneratorManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * PDF响应处理器（优化版）
 *
 * @author athena
 */
@Slf4j
@RequiredArgsConstructor
public class PdfResponseHandler implements HandlerMethodReturnValueHandler {
    /**
     * PDF文件名的最大长度，通常为255个字符。
     */
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    /**
     * 非法文件名字符的正则表达式，包含控制字符和常见非法字符。
     */
    private static final String ILLEGAL_FILENAME_CHARS = "[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]";
    /**
     * Content-Disposition 响应头格式，用于设置下载文件名。
     */
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"";
    /**
     * PDF生成器管理器，负责选择合适的PDF生成器。
     */
    private final PdfGeneratorManager generatorManager;

    /**
     * 判断方法返回值是否支持 @PdfResponse 注解。
     *
     * @param returnType 方法参数信息
     * @return 是否支持 @PdfResponse 注解
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(PdfResponse.class);
    }

    /**
     * 处理方法返回值，将其转换为PDF响应。
     *
     * @param returnValue  控制器返回的数据对象
     * @param returnType   方法参数信息
     * @param mavContainer 模型和视图容器
     * @param webRequest   原生Web请求对象
     * @throws Exception 处理过程中发生的异常
     */
    @Override
    public void handleReturnValue(Object returnValue,
                                  MethodParameter returnType,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        // 标记请求已被处理，防止其他处理器继续处理
        mavContainer.setRequestHandled(true);

        // 获取 @PdfResponse 注解配置
        PdfResponse pdfResponse = Optional.ofNullable(returnType.getMethodAnnotation(PdfResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("方法返回值必须使用@PdfResponse注解标记"));

        // 创建输出流并导出 Word 文件
        try (OutputStream outputStream = createOutputStream(webRequest, pdfResponse)) {
            generatorManager.generate(returnValue, pdfResponse, outputStream);
        } catch (IOException e) {
            log.error("导出 Word 文件时发生错误", e);
            throw e;
        }
    }

    /**
     * 创建 HTTP 响应输出流，并设置响应头信息。
     *
     * @param webRequest  当前 Web 请求
     * @param pdfResponse PDF导出注解信息
     * @return 输出流
     * @throws IOException IO 异常
     */
    private OutputStream createOutputStream(NativeWebRequest webRequest, PdfResponse pdfResponse) throws IOException {
        // 获取 HTTP 响应对象
        HttpServletResponse response = Optional.ofNullable(webRequest.getNativeResponse(HttpServletResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("无法获取 HttpServletResponse"));

        String fileName = pdfResponse.fileName();

        // 校验文件名合法性
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 设置响应头信息
        response.setContentType(PDF_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 清理文件名中的非法字符并进行 URL 编码
        String sanitizedFileName = fileName.replaceAll(ILLEGAL_FILENAME_CHARS, "_");
        String encodedFileName = URLUtil.encode(sanitizedFileName, StandardCharsets.UTF_8);
        String fullFileName = encodedFileName + ".pdf";

        // 设置文件下载相关的响应头
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, fullFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return response.getOutputStream();
    }

}

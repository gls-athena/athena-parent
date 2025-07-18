package com.gls.athena.starter.excel.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.generator.ExcelGeneratorManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Excel响应处理器，用于处理带有@ExcelResponse注解的方法返回值
 */
@Slf4j
@RequiredArgsConstructor
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

    /**
     * Excel文件的MIME类型
     */
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";

    /**
     * HTTP响应头Content-Disposition的格式模板
     */
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";

    /**
     * 文件名最大长度限制
     */
    private static final int MAX_FILENAME_LENGTH = 255;

    /**
     * 文件名中的非法字符正则表达式
     */
    private static final String ILLEGAL_FILENAME_CHARS = "[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]";

    /**
     * Excel生成管理器，用于生成Excel文件
     */
    private final ExcelGeneratorManager excelGeneratorManager;

    /**
     * 判断处理器是否支持处理该返回类型
     *
     * @param returnType 方法返回类型
     * @return 如果支持则返回true，否则返回false
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ExcelResponse.class);
    }

    /**
     * 处理方法返回值，将数据导出为Excel文件
     *
     * @param returnValue  方法返回值
     * @param returnType   方法返回类型
     * @param mavContainer ModelAndView容器
     * @param webRequest   Web请求
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void handleReturnValue(Object returnValue, @NonNull MethodParameter returnType,
                                  @NonNull ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest) throws Exception {
        // 标记请求已被处理，防止其他处理器继续处理
        mavContainer.setRequestHandled(true);

        // 获取@ExcelResponse注解配置
        ExcelResponse excelResponse = Optional.ofNullable(returnType.getMethodAnnotation(ExcelResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("方法返回值必须使用@ExcelResponse注解标记"));

        // 创建输出流并导出Excel文件
        try (OutputStream outputStream = createOutputStream(webRequest, excelResponse)) {
            excelGeneratorManager.generate(returnValue, excelResponse, outputStream);
        } catch (IOException e) {
            log.error("导出Excel文件时发生错误", e);
            throw e;
        }
    }

    /**
     * 创建用于导出Excel文件的输出流
     *
     * @param webRequest    Web请求
     * @param excelResponse Excel响应注解
     * @return 输出流
     * @throws IOException 如果创建输出流时发生错误
     */
    private OutputStream createOutputStream(NativeWebRequest webRequest, ExcelResponse excelResponse) throws IOException {
        // 获取HTTP响应对象
        HttpServletResponse response = Optional.ofNullable(webRequest.getNativeResponse(HttpServletResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("无法获取HttpServletResponse"));

        String fileName = excelResponse.filename();
        String excelType = excelResponse.excelType().getValue();

        // 验证文件名合法性
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (fileName.length() > MAX_FILENAME_LENGTH - excelType.length()) {
            throw new IllegalArgumentException("文件名过长");
        }

        // 设置响应头信息
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 清理文件名中的非法字符并进行URL编码
        String sanitizedFileName = fileName.replaceAll(ILLEGAL_FILENAME_CHARS, "_");
        String encodedFileName = URLUtil.encode(sanitizedFileName, StandardCharsets.UTF_8);
        String fullFileName = encodedFileName + excelType;

        // 设置文件下载相关的响应头
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, fullFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return response.getOutputStream();
    }

}

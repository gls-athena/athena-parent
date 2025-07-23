package com.gls.athena.starter.pdf.handler;

import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.generator.PdfGeneratorManager;
import com.gls.athena.starter.web.enums.FileEnums;
import com.gls.athena.starter.web.util.WebUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * PDF响应处理器（优化版）
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class PdfResponseHandler implements HandlerMethodReturnValueHandler {

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

        // 创建输出流并导出 PDF 文件
        try (OutputStream outputStream = WebUtil.createOutputStream(webRequest, pdfResponse.filename(), FileEnums.PDF)) {
            generatorManager.generate(returnValue, pdfResponse, outputStream);
        } catch (IOException e) {
            log.error("导出 PDF 文件时发生错误", e);
            throw e;
        }
    }

}

package com.gls.athena.starter.excel.handler;

import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.generator.ExcelGeneratorManager;
import com.gls.athena.starter.web.util.WebUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * Excel响应处理器，用于处理带有@ExcelResponse注解的方法返回值
 *
 * @author lizy19
 */
@Slf4j
@RequiredArgsConstructor
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

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
        try (OutputStream outputStream = WebUtil.createOutputStream(webRequest, excelResponse.filename(), excelResponse.excelType().getValue())) {
            excelGeneratorManager.generate(returnValue, excelResponse, outputStream);
        } catch (IOException e) {
            log.error("导出Excel文件时发生错误", e);
            throw e;
        }
    }

}

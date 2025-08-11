package com.gls.athena.starter.jasper.handler;

import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.jasper.generator.JasperGeneratorManager;
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
 * Jasper响应处理器 - 专门负责Spring MVC返回值处理
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class JasperResponseHandler implements HandlerMethodReturnValueHandler {

    private final JasperGeneratorManager generatorManager;

    /**
     * 判断是否支持返回类型
     *
     * @param returnType 方法参数对象，包含返回类型信息
     * @return true-如果方法被@JasperResponse注解标记，false-否则
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 检查方法是否包含JasperResponse注解来判断是否支持该返回类型
        return returnType.hasMethodAnnotation(JasperResponse.class);
    }

    /**
     * 处理返回值，生成并导出 Jasper 报表文件
     *
     * @param returnValue  方法返回值对象，用于生成报表的数据源
     * @param returnType   方法返回值类型参数，包含方法注解等元数据信息
     * @param mavContainer ModelAndView 容器，用于标记请求处理状态
     * @param webRequest   原生 Web 请求对象，用于获取 HTTP 响应输出流
     * @throws Exception 当报表生成或文件导出过程中发生错误时抛出
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 标记请求已被处理，防止其他处理器继续处理
        mavContainer.setRequestHandled(true);

        // 获取 @PdfResponse 注解配置
        JasperResponse jasperResponse = Optional.ofNullable(returnType.getMethodAnnotation(JasperResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("方法返回值必须使用@JasperResponse注解标记"));

        // 创建输出流并导出 Word 文件
        try (OutputStream outputStream = WebUtil.createOutputStream(webRequest, jasperResponse.filename(), jasperResponse.fileType())) {
            generatorManager.generate(returnValue, jasperResponse, outputStream);
        } catch (IOException e) {
            log.error("导出 Word 文件时发生错误", e);
            throw e;
        }
    }

}

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
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(JasperResponse.class);
    }

    /**
     * 处理返回值
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

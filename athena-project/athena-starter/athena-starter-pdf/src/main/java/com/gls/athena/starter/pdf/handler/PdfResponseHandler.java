package com.gls.athena.starter.pdf.handler;

import cn.hutool.core.bean.BeanUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.factory.PdfProcessingStrategyFactory;
import com.gls.athena.starter.pdf.strategy.PdfProcessingStrategy;
import com.gls.athena.starter.pdf.support.HttpResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.OutputStream;
import java.util.Map;

/**
 * PDF响应处理器
 * 应用策略模式处理不同类型的PDF生成
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class PdfResponseHandler implements HandlerMethodReturnValueHandler {

    private final PdfProcessingStrategyFactory strategyFactory;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(PdfResponse.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);

        PdfResponse pdfResponse = returnType.getMethodAnnotation(PdfResponse.class);
        log.debug("处理PDF响应: {}", pdfResponse);

        Map<String, Object> data = BeanUtil.beanToMap(returnValue);

        try (OutputStream outputStream = HttpResponseUtil.getOutputStream(webRequest, pdfResponse.filename())) {
            // 使用策略模式处理不同类型的PDF生成
            PdfProcessingStrategy strategy = strategyFactory.getStrategy(pdfResponse.templateType().getCode());
            strategy.process(data, pdfResponse.template(), outputStream);
        } catch (Exception e) {
            log.error("PDF生成失败: {}", e.getMessage(), e);
            throw new RuntimeException("PDF生成失败", e);
        }
    }
}

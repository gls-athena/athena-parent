package com.gls.athena.starter.pdf.handler;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.support.PdfUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.OutputStream;

/**
 * PDF响应处理器
 *
 * @author lizy19
 */
@Slf4j
public class PdfResponseHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        //  检查方法是否带有@PdfResponse注
        return returnType.hasMethodAnnotation(PdfResponse.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 获取方法上的@PdfResponse注解
        PdfResponse pdfResponse = returnType.getMethodAnnotation(PdfResponse.class);
        log.info("处理PDF响应: {}", pdfResponse);
        try (OutputStream outputStream = PdfUtil.getOutputStream(webRequest, pdfResponse.filename())) {
            if (StrUtil.isEmpty(pdfResponse.template())) {
                fillDataToPdf(returnValue, outputStream, pdfResponse);
            } else {
                writeDataToPdf(returnValue, outputStream, pdfResponse);
            }
        }

    }

    private void fillDataToPdf(Object returnValue, OutputStream outputStream, PdfResponse pdfResponse) {
    }

    private void writeDataToPdf(Object returnValue, OutputStream outputStream, PdfResponse pdfResponse) {
    }

}

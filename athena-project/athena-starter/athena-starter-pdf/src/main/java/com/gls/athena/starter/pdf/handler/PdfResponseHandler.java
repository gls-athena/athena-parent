package com.gls.athena.starter.pdf.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.template.TemplateUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfProperties;
import com.gls.athena.starter.pdf.support.PdfUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * PDF响应处理器
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class PdfResponseHandler implements HandlerMethodReturnValueHandler {
    /**
     * PDF属性
     */
    private final PdfProperties pdfProperties;

    /**
     * 检查方法是否带有@PdfResponse注解
     *
     * @param returnType 方法参数
     * @return 是否支持
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        //  检查方法是否带有@PdfResponse注
        return returnType.hasMethodAnnotation(PdfResponse.class);
    }

    /**
     * 处理方法返回值
     *
     * @param returnValue  方法返回值
     * @param returnType   方法参数
     * @param mavContainer 模型和视图容器
     * @param webRequest   web请求
     * @throws Exception 异常
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 设置请求已处理
        mavContainer.setRequestHandled(true);
        // 获取方法上的@PdfResponse注解
        PdfResponse pdfResponse = returnType.getMethodAnnotation(PdfResponse.class);
        log.info("处理PDF响应: {}", pdfResponse);
        Map<String, Object> data = BeanUtil.beanToMap(returnValue);
        try (OutputStream outputStream = PdfUtil.getOutputStream(webRequest, pdfResponse.filename())) {
            switch (pdfResponse.templateType()) {
                case HTML:
                    // 如果是HTML模板，直接渲染HTML到PDF
                    handleHtmlTemplate(data, outputStream, pdfResponse);
                    break;
                case PDF:
                    // 如果是PDF模板，填充数据到PDF
                    handlePdfTemplate(data, outputStream, pdfResponse);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的模板类型: " + pdfResponse.templateType());
            }
        }
    }

    /**
     * 填充数据到PDF
     *
     * @param data         方法返回值
     * @param outputStream 输出流
     * @param pdfResponse  PDF响应
     */
    private void handleHtmlTemplate(Map<String, Object> data, OutputStream outputStream, PdfResponse pdfResponse) {
        // 渲染模板
        String html = TemplateUtil.createEngine(pdfProperties.getTemplateConfig())
                .getTemplate(pdfResponse.template())
                .render(data);
        // 将HTML写入PDF
        PdfUtil.writeHtmlToPdf(html, outputStream);
    }

    /**
     * 将数据写入PDF
     *
     * @param data         方法返回值
     * @param outputStream 输出流
     * @param pdfResponse  PDF响应
     */
    @SneakyThrows
    private void handlePdfTemplate(Map<String, Object> data, OutputStream outputStream, PdfResponse pdfResponse) {
        InputStream template = new ClassPathResource(pdfResponse.template()).getInputStream();
        PdfUtil.fillPdfTemplate(template, data, outputStream);
    }

}

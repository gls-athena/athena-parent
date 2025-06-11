package com.gls.athena.starter.pdf.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfProperties;
import com.gls.athena.starter.pdf.support.PdfUtil;
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

    /**
     * 填充数据到PDF
     *
     * @param returnValue  方法返回值
     * @param outputStream 输出流
     * @param pdfResponse  PDF响应
     */
    private void fillDataToPdf(Object returnValue, OutputStream outputStream, PdfResponse pdfResponse) {
        // 转换数据为Map
        Map<String, Object> data = BeanUtil.beanToMap(returnValue);
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
     * @param returnValue  方法返回值
     * @param outputStream 输出流
     * @param pdfResponse  PDF响应
     */
    private void writeDataToPdf(Object returnValue, OutputStream outputStream, PdfResponse pdfResponse) {
        // todo 待实现
    }

}

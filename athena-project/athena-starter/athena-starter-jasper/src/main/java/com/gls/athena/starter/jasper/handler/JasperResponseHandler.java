package com.gls.athena.starter.jasper.handler;

import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.jasper.service.DataConversionService;
import com.gls.athena.starter.jasper.service.JasperReportService;
import com.gls.athena.starter.jasper.service.JasperResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Jasper响应处理器 - 专门负责Spring MVC返回值处理
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class JasperResponseHandler implements HandlerMethodReturnValueHandler {

    private final JasperReportService jasperReportService;
    private final JasperResponseService jasperResponseService;
    private final DataConversionService dataConversionService;

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
        // 标记请求已处理
        mavContainer.setRequestHandled(true);

        // 获取注解配置
        JasperResponse jasperResponse = getJasperResponse(returnType);

        // 转换数据格式
        Map<String, Object> reportData = dataConversionService.convertToReportData(returnValue);

        // 验证数据完整性
        if (!dataConversionService.validateReportData(reportData)) {
            throw new IllegalArgumentException("报告数据验证失败");
        }

        // 处理报告生成和响应
        processReportGeneration(reportData, jasperResponse, webRequest);
    }

    /**
     * 获取JasperResponse注解
     */
    private JasperResponse getJasperResponse(MethodParameter returnType) {
        JasperResponse jasperResponse = returnType.getMethodAnnotation(JasperResponse.class);
        if (jasperResponse == null) {
            throw new IllegalArgumentException("方法未添加@JasperResponse注解");
        }
        return jasperResponse;
    }

    /**
     * 处理报告生成
     */
    private void processReportGeneration(Map<String, Object> reportData, JasperResponse jasperResponse,
                                         NativeWebRequest webRequest) throws IOException {
        try (OutputStream outputStream = jasperResponseService.configureResponseAndGetOutputStream(webRequest, jasperResponse)) {
            jasperReportService.generateReport(reportData, outputStream, jasperResponse);
        } catch (IOException e) {
            log.error("处理Jasper响应失败: {}", jasperResponse.template(), e);
            throw new RuntimeException("处理失败", e);
        }
    }
}

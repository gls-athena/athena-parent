package com.gls.athena.starter.jasper.service;

import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.jasper.strategy.IReportHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Jasper报告生成服务 - 专门负责报告的生成逻辑
 *
 * @author george
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JasperReportService {

    private final List<IReportHandler> reportHandlers;

    /**
     * 生成报告
     *
     * @param data           报告数据
     * @param outputStream   输出流
     * @param jasperResponse 响应配置
     * @throws IOException 处理异常
     */
    public void generateReport(Map<String, Object> data, OutputStream outputStream, JasperResponse jasperResponse) throws IOException {
        try {
            IReportHandler handler = findReportHandler(jasperResponse);
            handler.handle(data, outputStream, jasperResponse);
            log.info("报告生成成功: {}", jasperResponse.template());
        } catch (Exception e) {
            log.error("报告生成失败: {}", jasperResponse.template(), e);
            throw new IOException("报告生成失败", e);
        }
    }

    /**
     * 查找适合的报告处理器
     *
     * @param jasperResponse 响应配置
     * @return 报告处理器
     */
    private IReportHandler findReportHandler(JasperResponse jasperResponse) {
        return reportHandlers.stream()
                .filter(handler -> handler.supports(jasperResponse.reportType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("找不到支持的报告处理器: " + jasperResponse.reportType()));
    }
}

package com.gls.athena.starter.jasper.support;

import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.jasper.strategy.IReportHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * PDF处理助手类，使用策略模式管理不同类型的PDF模板处理
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JasperHelper {

    private final List<IReportHandler> reportHandlers;

    private IReportHandler getReportHandler(JasperResponse jasperResponse) {
        return reportHandlers.stream()
                .filter(handler -> handler.supports(jasperResponse.reportType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No template handler found for report type: " + jasperResponse.reportType()));
    }

    public void handle(Map<String, Object> data, OutputStream outputStream, JasperResponse jasperResponse) throws IOException {

        IReportHandler handler = getReportHandler(jasperResponse);

        handler.handle(data, outputStream, jasperResponse);
    }
}

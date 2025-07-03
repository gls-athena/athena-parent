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

    /**
     * 根据JasperResponse获取对应的报告处理器
     *
     * @param jasperResponse Jasper响应对象，包含报告类型信息
     * @return IReportHandler接口的实现类，用于处理特定类型的报告
     * @throws IllegalArgumentException 如果找不到支持指定报告类型的处理器，则抛出此异常
     */
    private IReportHandler getReportHandler(JasperResponse jasperResponse) {
        // 从报告处理器集合中寻找支持指定报告类型的处理器
        return reportHandlers.stream()
                .filter(handler -> handler.supports(jasperResponse.reportType()))
                .findFirst()
                // 如果找不到支持的处理器，抛出IllegalArgumentException异常
                .orElseThrow(() -> new IllegalArgumentException("No template handler found for report type: " + jasperResponse.reportType()));
    }

    /**
     * 处理报告生成请求
     * <p>
     * 该方法负责将传入的数据处理并生成相应的报告输出到指定的输出流中
     * 它通过使用一个报告处理器（IReportHandler）来完成实际的数据处理和报告生成工作
     *
     * @param data           包含报告所需数据的键值对字典
     * @param outputStream   报告输出的目标输出流
     * @param jasperResponse 包含响应信息的JasperResponse对象，用于传递额外的响应参数或属性
     * @throws IOException 如果在处理输出流时发生I/O错误
     */
    public void handle(Map<String, Object> data, OutputStream outputStream, JasperResponse jasperResponse) throws IOException {

        // 获取报告处理器实例
        IReportHandler handler = getReportHandler(jasperResponse);

        // 使用数据、输出流和响应对象调用处理器的处理方法
        handler.handle(data, outputStream, jasperResponse);
    }
}

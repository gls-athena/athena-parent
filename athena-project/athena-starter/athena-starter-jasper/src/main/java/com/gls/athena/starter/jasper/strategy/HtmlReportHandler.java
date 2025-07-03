package com.gls.athena.starter.jasper.strategy;

import com.gls.athena.starter.jasper.config.ReportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * HTML报告处理器
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlReportHandler implements IReportHandler {

    /**
     * 将JasperPrint对象导出为XML格式的报告并写入到输出流中
     *
     * @param jasperPrint  JasperPrint对象，代表已编译的报告
     * @param outputStream 输出流，用于写入导出的XML报告
     * @throws JRException 如果导出过程中发生错误，抛出JRException异常
     */
    @Override
    public void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        // 使用JasperExportManager将报告导出为XML格式并写入到指定的输出流
        JasperExportManager.exportReportToXmlStream(jasperPrint, outputStream);
    }

    /**
     * 判断当前报告生成器是否支持指定的报告类型
     *
     * @param reportType 报告类型，用于判断是否支持
     * @return 如果支持指定的报告类型返回true，否则返回false
     */
    @Override
    public boolean supports(ReportType reportType) {
        // 仅支持HTML类型的报告
        return ReportType.HTML.equals(reportType);
    }
}

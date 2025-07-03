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
 * PDF文档模板处理策略
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdfReportHandler implements IReportHandler {

    /**
     * 将JasperPrint对象导出为PDF格式并写入到输出流中
     *
     * @param jasperPrint  JasperPrint对象，代表已编译的Jasper报告
     * @param outputStream 输出流，用于接收导出的PDF报告数据
     * @throws JRException 如果导出过程中发生错误，将抛出JRException异常
     */
    @Override
    public void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        // 使用JasperExportManager将JasperPrint对象导出为PDF格式，并将结果写入到指定的输出流中
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
    }

    /**
     * 判断当前处理器支持的报告类型
     *
     * @param reportType 报告类型枚举，表示要处理的报告类型
     * @return 如果当前处理器支持PDF类型的报告，则返回true；否则返回false
     */
    @Override
    public boolean supports(ReportType reportType) {
        // 仅支持PDF类型的报告
        return ReportType.PDF.equals(reportType);
    }
}

package com.gls.athena.starter.jasper.strategy;

import com.gls.athena.starter.jasper.config.ReportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * DOCX报告处理器
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocxReportHandler implements IReportHandler {

    /**
     * 将JasperPrint对象导出为Word文档格式
     *
     * @param jasperPrint  填充后的JasperPrint对象，包含要导出的报告数据
     * @param outputStream 输出流，用于写入导出的Word文档数据
     * @throws JRException 如果导出过程中发生错误，将抛出JRException异常
     */
    @Override
    public void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        // 创建一个JRDocxExporter实例，用于导出Word文档
        JRDocxExporter exporter = new JRDocxExporter();

        // 设置导出器的输入源为填充后的JasperPrint对象
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

        // 设置导出器的输出目标为提供的输出流
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        // 执行报告导出操作
        exporter.exportReport();
    }

    /**
     * 判断当前处理器支持的报告类型
     *
     * @param reportType 报告模板类型，用于判断是否支持处理该类型的报告
     * @return 如果支持指定类型的报告，则返回true；否则返回false
     */
    @Override
    public boolean supports(ReportType reportType) {
        // 仅支持处理DOCX类型的报告
        return ReportType.DOCX.equals(reportType);
    }
}

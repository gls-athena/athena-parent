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
 * jasper模板处理
 *
 * @author lizy19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocxReportHandler implements IReportHandler {

    @Override
    public void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
    }

    @Override
    public boolean supports(ReportType templateType) {
        return ReportType.DOCX.equals(templateType);
    }
}

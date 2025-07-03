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

    @Override
    public void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
    }

    @Override
    public boolean supports(ReportType templateType) {
        return ReportType.PDF.equals(templateType);
    }
}

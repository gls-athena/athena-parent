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
 * HTML模板处理策略
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlReportHandler implements IReportHandler {

    @Override
    public void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JasperExportManager.exportReportToXmlStream(jasperPrint, outputStream);
    }

    @Override
    public boolean supports(ReportType templateType) {
        return ReportType.HTML.equals(templateType);
    }
}

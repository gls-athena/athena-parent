package com.gls.athena.starter.pdf.strategy;

import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfTemplateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * jasper模板处理
 *
 * @author lizy19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JasperTemplateHandler implements ITemplateHandler {
    /**
     * 重写处理方法以生成PDF响应
     * 该方法负责从给定的数据填充PDF模板，并将生成的PDF输出到指定的输出流中
     *
     * @param data         包含报告所需数据的键值对映射
     * @param outputStream 用于输出生成的PDF数据的流
     * @param pdfResponse  包含PDF模板信息的响应对象
     * @throws IOException 如果输出流发生错误
     */
    @Override
    public void handle(Map<String, Object> data, OutputStream outputStream, PdfResponse pdfResponse) throws IOException {
        try {
            // 加载PDF模板资源
            InputStream template = new ClassPathResource(pdfResponse.template()).getInputStream();

            // 从模板输入流中加载JasperReport对象
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(template);

            // 使用给定的数据填充报告，使用空数据源
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, data, new JREmptyDataSource());

            // 将填充后的报告导出到PDF流
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            JRDocxExporter jrDocxExporter = new JRDocxExporter();
            jrDocxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            jrDocxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            jrDocxExporter.exportReport();
        } catch (JRException e) {
            // 日志记录jasper模板错误
            log.error("jasper template error", e);
            // 将模板处理异常包装为运行时异常并重新抛出
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean supports(PdfTemplateType templateType) {
        return PdfTemplateType.JASPER.equals(templateType);
    }
}

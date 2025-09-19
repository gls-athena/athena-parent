package com.gls.athena.starter.jasper.generator;

import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.jasper.annotation.JasperResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * 实现JasperGenerator接口，专用于生成Word文档（.docx）的报告导出器
 *
 * @author george
 */
@Component
public class DocxJasperGenerator implements JasperGenerator {
    /**
     * 导出报告为Word文档格式
     *
     * @param jasperPrint  填充后的JasperPrint对象，包含报告的数据和样式
     * @param outputStream 输出流，用于写入生成的Word文档
     * @throws JRException 如果导出过程中发生错误，抛出JRException
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
     * 判断当前导出器是否支持指定的JasperResponse
     *
     * @param jasperResponse 包含文件类型和导出器信息的JasperResponse对象
     * @return 如果当前导出器支持指定的文件类型和导出器信息，则返回true，否则返回false
     */
    @Override
    public boolean supports(JasperResponse jasperResponse) {
        // 检查文件类型是否为Word文档（.docx），并且导出器类型为JasperGenerator
        return FileTypeEnums.DOCX.equals(jasperResponse.fileType())
                && jasperResponse.generator() == JasperGenerator.class;
    }

}

/**
 * PDF报表生成器，用于将JasperReports报表导出为PDF格式
 * 实现了JasperGenerator接口，提供具体的PDF报表导出功能
 */
package com.gls.athena.starter.jasper.generator;

import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.web.enums.FileEnums;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * PDF报表生成器类
 * 提供将JasperReports报表导出到PDF格式文件或输出流的方法
 *
 * @author george
 */
@Component
public class PdfJasperGenerator implements JasperGenerator {
    /**
     * 将报表导出到PDF格式的输出流中
     *
     * @param jasperPrint  填充后的报表对象，包含报表的数据和样式
     * @param outputStream 输出流，用于接收导出的PDF报表数据
     * @throws JRException 如果导出过程中发生错误，将抛出此异常
     */
    @Override
    public void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
    }

    /**
     * 判断当前生成器是否支持指定的JasperResponse
     *
     * @param jasperResponse 包含报表响应信息的注解对象，用于判断是否支持当前的报表导出请求
     * @return 如果当前生成器支持指定的JasperResponse，则返回true；否则返回false
     */
    @Override
    public boolean supports(JasperResponse jasperResponse) {
        return FileEnums.PDF.equals(jasperResponse.fileType())
                && jasperResponse.generator() == JasperGenerator.class;
    }
}

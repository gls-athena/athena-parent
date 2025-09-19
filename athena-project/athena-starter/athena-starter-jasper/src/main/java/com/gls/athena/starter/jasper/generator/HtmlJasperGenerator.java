/**
 * HTML Jasper生成器类，实现JasperGenerator接口
 * 该类专门用于将Jasper报告导出为HTML格式
 */
package com.gls.athena.starter.jasper.generator;

import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.jasper.annotation.JasperResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * HTML Jasper生成器类，实现JasperGenerator接口
 *
 * @author george
 */
@Component
public class HtmlJasperGenerator implements JasperGenerator {
    /**
     * 导出JasperPrint对象到HTML格式的输出流
     *
     * @param jasperPrint  填充后的Jasper报告对象，包含报告的数据和样式
     * @param outputStream 输出流，用于接收导出的HTML报告数据
     * @throws JRException 当导出过程中发生错误时抛出此异常
     */
    @Override
    public void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        // 创建HtmlExporter实例，用于导出HTML格式的报告
        HtmlExporter exporter = new HtmlExporter();

        // 设置导出器的输入，包括填充后的Jasper报告对象
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

        // 设置导出器的输出，指定输出流以接收HTML格式的报告数据
        exporter.setExporterOutput(new SimpleHtmlExporterOutput(outputStream));

        // 执行报告的导出操作
        exporter.exportReport();
    }

    /**
     * 判断当前生成器是否支持指定的JasperResponse
     *
     * @param jasperResponse 包含文件类型和生成器信息的响应对象
     * @return 如果当前生成器支持指定的文件类型和生成器，则返回true；否则返回false
     */
    @Override
    public boolean supports(JasperResponse jasperResponse) {
        // 判断文件类型是否为HTML且生成器类型是否为JasperGenerator
        return FileTypeEnums.HTML.equals(jasperResponse.fileType())
                && jasperResponse.generator() == JasperGenerator.class;
    }

}

/**
 * HTML Jasper生成器类，实现JasperGenerator接口
 * 该类专门用于将Jasper报告导出为HTML格式
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
        JasperExportManager.exportReportToXmlStream(jasperPrint, outputStream);
    }

    /**
     * 判断当前生成器是否支持指定的JasperResponse
     *
     * @param jasperResponse 包含文件类型和生成器信息的响应对象
     * @return 如果当前生成器支持指定的文件类型和生成器，则返回true；否则返回false
     */
    @Override
    public boolean supports(JasperResponse jasperResponse) {
        return FileEnums.HTML.equals(jasperResponse.fileType())
                && jasperResponse.generator() == JasperGenerator.class;
    }
}

package com.gls.athena.starter.jasper.strategy;

import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.jasper.config.ReportType;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * PDF模板处理策略接口
 *
 * @author george
 */
public interface IReportHandler {

    /**
     * 根据指定的数据和模板生成报告，并将报告输出到指定的输出流中
     * 此方法使用JasperReports库来处理报告的生成和导出
     *
     * @param data           包含报告所需数据的映射，键为字段名，值为字段值
     * @param outputStream   报告输出的目标输出流
     * @param jasperResponse 包含模板信息和响应处理程序的对象
     * @throws IOException 如果在读取模板或输出报告时发生I/O错误
     */
    default void handle(Map<String, Object> data, OutputStream outputStream, JasperResponse jasperResponse) throws IOException {
        try {
            // 加载模板文件为输入流
            InputStream template = new ClassPathResource(jasperResponse.template()).getInputStream();

            // 从模板输入流中加载JasperReport对象
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(template);

            // 使用提供的数据填充报告，生成JasperPrint对象
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, data, new JREmptyDataSource());

            // 导出报告到指定的输出流
            exportReport(jasperPrint, outputStream);
        } catch (JRException e) {
            // 如果在报告处理过程中发生错误，将其包装为RuntimeException并抛出
            throw new RuntimeException(e);
        }
    }

    /**
     * 导出报告到指定的输出流
     *
     * @param jasperPrint  要导出的JasperPrint对象
     * @param outputStream 输出报告目标输出流
     * @throws JRException 如果在导出报告时发生错误
     */
    void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException;

    /**
     * 检查当前处理策略是否支持指定的报告类型
     *
     * @param reportType 要检查的报表类型
     * @return 如果支持指定的报告类型，则返回true；否则返回false
     */
    boolean supports(ReportType reportType);
}

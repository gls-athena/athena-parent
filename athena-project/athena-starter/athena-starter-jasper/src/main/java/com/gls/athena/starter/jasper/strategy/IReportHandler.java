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
     * 处理PDF模板并输出
     *
     * @param data           数据映射
     * @param outputStream   输出流
     * @param jasperResponse PDF响应注解
     * @throws IOException 如果处理过程中发生I/O错误
     */
    default void handle(Map<String, Object> data, OutputStream outputStream, JasperResponse jasperResponse) throws IOException {
        try {
            // 加载PDF模板资源
            InputStream template = new ClassPathResource(jasperResponse.template()).getInputStream();

            // 从模板输入流中加载JasperReport对象
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(template);

            // 使用给定的数据填充报告，使用空数据源
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, data, new JREmptyDataSource());

            exportReport(jasperPrint, outputStream);
        } catch (JRException e) {
            // 将模板处理异常包装为运行时异常并重新抛出
            throw new RuntimeException(e);
        }
    }

    /**
     * 导出报告到输出流
     *
     * @param jasperPrint  JasperPrint对象
     * @param outputStream 输出流
     * @throws JRException 如果导出过程中发生错误
     */
    void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException;

    /**
     * 是否支持此类型的模板
     *
     * @param templateType 模板类型
     * @return 是否支持
     */
    boolean supports(ReportType templateType);
}

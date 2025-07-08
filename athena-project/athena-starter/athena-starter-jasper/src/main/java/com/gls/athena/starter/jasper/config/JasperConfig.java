package com.gls.athena.starter.jasper.config;

import com.gls.athena.starter.jasper.handler.JasperResponseHandler;
import com.gls.athena.starter.jasper.service.DataConversionService;
import com.gls.athena.starter.jasper.service.JasperReportService;
import com.gls.athena.starter.jasper.service.JasperResponseService;
import com.gls.athena.starter.jasper.strategy.IReportHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Jasper报告配置类 - 专门负责Bean的配置和注册
 *
 * @author george
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JasperConfig {

    /**
     * 创建数据转换服务Bean
     */
    @Bean
    public DataConversionService dataConversionService() {
        return new DataConversionService();
    }

    /**
     * 创建Jasper报告服务Bean
     */
    @Bean
    public JasperReportService jasperReportService(List<IReportHandler> reportHandlers) {
        return new JasperReportService(reportHandlers);
    }

    /**
     * 创建Jasper响应服务Bean
     */
    @Bean
    public JasperResponseService jasperResponseService() {
        return new JasperResponseService();
    }

    /**
     * 创建Jasper响应处理器Bean
     */
    @Bean
    public JasperResponseHandler jasperResponseHandler(JasperReportService jasperReportService,
                                                       JasperResponseService jasperResponseService,
                                                       DataConversionService dataConversionService) {
        return new JasperResponseHandler(jasperReportService, jasperResponseService, dataConversionService);
    }
}

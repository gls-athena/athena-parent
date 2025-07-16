package com.gls.athena.starter.pdf.config;

import com.gls.athena.starter.pdf.generator.PdfGeneratorManager;
import com.gls.athena.starter.pdf.handler.PdfResponseHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * PDF配置类（简化版）
 *
 * @author athena
 */
@Configuration
@EnableConfigurationProperties(PdfProperties.class)
public class PdfConfig {

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Resource
    private PdfGeneratorManager pdfGeneratorManager;

    @PostConstruct
    public void init() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(new PdfResponseHandler(pdfGeneratorManager));
        if (returnValueHandlers != null) {
            newHandlers.addAll(returnValueHandlers);
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }
}

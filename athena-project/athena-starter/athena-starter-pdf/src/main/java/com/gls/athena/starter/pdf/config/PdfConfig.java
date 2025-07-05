package com.gls.athena.starter.pdf.config;

import com.gls.athena.starter.pdf.factory.PdfProcessingStrategyFactory;
import com.gls.athena.starter.pdf.handler.PdfResponseHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * PDF配置类
 * 应用依赖注入和配置模式
 *
 * @author george
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class PdfConfig {

    @Resource
    private RequestMappingHandlerAdapter handlerAdapter;

    @Resource
    private PdfProcessingStrategyFactory strategyFactory;

    @PostConstruct
    public void init() {
        initReturnValueHandlers();
    }

    private void initReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();
        handlers.add(new PdfResponseHandler(strategyFactory));

        if (handlerAdapter.getReturnValueHandlers() != null) {
            handlers.addAll(handlerAdapter.getReturnValueHandlers());
        }

        handlerAdapter.setReturnValueHandlers(handlers);
    }
}

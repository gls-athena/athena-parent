package com.gls.athena.starter.jasper.config;

import com.gls.athena.starter.jasper.handler.JasperResponseHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring MVC集成配置 - 专门负责将Jasper处理器集成到Spring MVC中
 *
 * @author george
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RequiredArgsConstructor
public class JasperMvcIntegration {

    private final RequestMappingHandlerAdapter handlerAdapter;
    private final JasperResponseHandler jasperResponseHandler;

    /**
     * 初始化返回值处理器
     */
    @PostConstruct
    public void initializeReturnValueHandlers() {
        log.info("正在初始化Jasper返回值处理器...");

        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();
        handlers.add(jasperResponseHandler);

        // 添加现有的处理器
        if (handlerAdapter.getReturnValueHandlers() != null) {
            handlers.addAll(handlerAdapter.getReturnValueHandlers());
        }

        handlerAdapter.setReturnValueHandlers(handlers);
        log.info("Jasper返回值处理器初始化完成");
    }
}

package com.gls.athena.starter.jasper.config;

import com.gls.athena.starter.jasper.handler.JasperResponseHandler;
import com.gls.athena.starter.jasper.support.JasperHelper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author george
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JasperConfig {
    /**
     * 请求处理适配器
     */
    @Resource
    private RequestMappingHandlerAdapter handlerAdapter;
    /**
     * pdf配置
     */
    @Resource
    private JasperHelper jasperHelper;

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        // 初始化返回值处理器，用于处理返回的Excel相关数据
        initReturnValueHandlers();
    }

    /**
     * 初始化返回值处理器
     */
    private void initReturnValueHandlers() {
        // 创建一个新的返回值处理器列表，并添加默认的ExcelResponseHandler
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();
        handlers.add(new JasperResponseHandler(jasperHelper));

        // 如果handlerAdapter中已经存在返回值处理器，则将其添加到列表中
        if (handlerAdapter.getReturnValueHandlers() != null) {
            handlers.addAll(handlerAdapter.getReturnValueHandlers());
        }

        // 将配置好的处理器列表设置到handlerAdapter中
        handlerAdapter.setReturnValueHandlers(handlers);
    }
}

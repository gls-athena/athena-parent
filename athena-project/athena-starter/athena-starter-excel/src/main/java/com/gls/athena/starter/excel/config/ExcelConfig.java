package com.gls.athena.starter.excel.config;

import com.gls.athena.starter.excel.handler.ExcelRequestHandler;
import com.gls.athena.starter.excel.handler.ExcelResponseHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel自动配置类
 * <p>
 * 用于配置Excel相关的请求处理器和响应处理器
 * 自动注册到Spring MVC的处理链中
 * </p>
 *
 * @author george
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ExcelConfig {

    @Resource
    private RequestMappingHandlerAdapter handlerAdapter;

    /**
     * 初始化Excel处理器配置
     * 将Excel专用的处理器添加到Spring MVC处理链的前端
     */
    @PostConstruct
    public void init() {
        initArgumentResolvers();
        initReturnValueHandlers();
    }

    /**
     * 初始化参数解析器
     */
    private void initArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new ExcelRequestHandler());
        if (handlerAdapter.getArgumentResolvers() != null) {
            resolvers.addAll(handlerAdapter.getArgumentResolvers());
        }
        handlerAdapter.setArgumentResolvers(resolvers);
    }

    /**
     * 初始化返回值处理器
     */
    private void initReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();
        handlers.add(new ExcelResponseHandler());
        if (handlerAdapter.getReturnValueHandlers() != null) {
            handlers.addAll(handlerAdapter.getReturnValueHandlers());
        }
        handlerAdapter.setReturnValueHandlers(handlers);
    }
}

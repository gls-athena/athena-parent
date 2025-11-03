package com.gls.athena.starter.excel.config;

import com.gls.athena.starter.excel.handler.ExcelRequestHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel自动配置类
 * <p>
 * 用于配置Excel相关的请求处理器和响应处理器，自动注册到Spring MVC的处理链中。
 * 同时提供默认的服务实现、线程池配置以及初始化处理链扩展逻辑。
 * </p>
 *
 * @author george
 */
@Configuration
@EnableConfigurationProperties(ExcelProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ExcelConfig {

    @Resource
    private RequestMappingHandlerAdapter handlerAdapter;

    /**
     * 初始化Excel处理器配置
     * 该方法在Bean初始化时调用，用于将Excel专用的处理器添加到Spring MVC处理链的前端。
     * 具体操作包括初始化参数解析器和返回值处理器。
     * <p>
     * 该方法通过@PostConstruct注解标记，确保在Bean初始化完成后立即执行。
     * <p>
     * 无参数
     * 无返回值
     */
    @PostConstruct
    public void init() {
        // 初始化参数解析器，用于处理请求中的Excel相关参数
        initArgumentResolvers();

    }

    /**
     * 初始化参数解析器。
     * <p>
     * 该方法用于初始化并配置处理请求时的参数解析器。首先创建一个包含默认解析器（如ExcelRequestHandler）的列表，
     * 然后如果当前handlerAdapter已经存在其他参数解析器，则将这些解析器添加到列表中。
     * 最后，将配置好的解析器列表设置到handlerAdapter中，以便在处理请求时使用。
     */
    private void initArgumentResolvers() {
        // 创建一个新的参数解析器列表，并添加默认的ExcelRequestHandler解析器
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new ExcelRequestHandler());

        // 如果handlerAdapter中已经存在其他参数解析器，则将其添加到列表中
        if (handlerAdapter.getArgumentResolvers() != null) {
            resolvers.addAll(handlerAdapter.getArgumentResolvers());
        }

        // 将配置好的解析器列表设置到handlerAdapter中
        handlerAdapter.setArgumentResolvers(resolvers);
    }

}

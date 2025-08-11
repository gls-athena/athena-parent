package com.gls.athena.starter.jasper.config;

import com.gls.athena.starter.jasper.generator.JasperGeneratorManager;
import com.gls.athena.starter.jasper.handler.JasperResponseHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Jasper报告配置类 - 专门负责Bean的配置和注册
 *
 * @author george
 */
@AutoConfiguration
@EnableConfigurationProperties(JasperProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JasperConfig {
    /**
     * 注入RequestMappingHandlerAdapter以修改返回值处理器
     */
    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    /**
     * 注入JasperGeneratorManager用于管理Jasper报告生成
     */
    @Resource
    private JasperGeneratorManager jasperGeneratorManager;

    /**
     * 在Bean初始化完成后执行的方法
     * 该方法将JasperResponseHandler添加到返回值处理器列表中
     *
     * @return 无返回值
     */
    @PostConstruct
    public void init() {
        // 获取现有的返回值处理器列表
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();

        // 创建新的处理器列表，首先添加JasperResponseHandler
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(new JasperResponseHandler(jasperGeneratorManager));

        // 如果存在原有处理器列表，则将其全部添加到新列表中
        if (returnValueHandlers != null) {
            newHandlers.addAll(returnValueHandlers);
        }

        // 将新的处理器列表设置回RequestMappingHandlerAdapter
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }

}

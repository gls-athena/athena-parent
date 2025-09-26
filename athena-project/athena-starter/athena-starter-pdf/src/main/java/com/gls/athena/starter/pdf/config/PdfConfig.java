package com.gls.athena.starter.pdf.config;

import com.gls.athena.starter.pdf.generator.PdfGenerator;
import com.gls.athena.starter.pdf.support.PdfResponseHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * PDF配置类
 *
 * @author george
 */
@Configuration
@EnableConfigurationProperties(PdfProperties.class)
public class PdfConfig {

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Resource
    private List<PdfGenerator> pdfGenerators;

    /**
     * 初始化方法，在Bean构造完成后执行
     * 该方法用于注册PDF响应处理器，将其添加到Spring MVC的返回值处理器链中
     * 确保PDF生成功能能够正确处理控制器方法的返回值
     */
    @PostConstruct
    public void init() {
        // 获取当前的返回值处理器列表
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();

        // 创建新的处理器列表，首先添加PDF响应处理器
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(new PdfResponseHandler(pdfGenerators));

        // 将原有的返回值处理器添加到列表中，保持原有的处理逻辑
        if (returnValueHandlers != null) {
            newHandlers.addAll(returnValueHandlers);
        }

        // 设置更新后的返回值处理器列表
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }
}


package com.gls.athena.starter.word.config;

import com.gls.athena.starter.word.generator.WordGenerator;
import com.gls.athena.starter.word.support.WordResponseHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Word配置类
 *
 * @author george
 */
@Configuration
@EnableConfigurationProperties(WordProperties.class)
public class WordConfig {
    /**
     * 请求映射处理器适配器，用于处理返回值
     */
    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    /**
     * Word生成器管理器，用于管理Word生成器
     */
    @Resource
    private List<WordGenerator> wordGenerators;

    /**
     * 初始化方法，用于在配置类加载后执行额外的初始化逻辑
     * 主要目的是为请求映射处理器适配器添加一个新的返回值处理器
     */
    @PostConstruct
    public void init() {
        // 获取现有的返回值处理器列表
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        // 创建新的处理器列表，首先添加自定义的Word响应处理器
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(new WordResponseHandler(wordGenerators));
        // 如果存在原有的处理器列表，将其全部添加到新的处理器列表中
        if (returnValueHandlers != null) {
            newHandlers.addAll(returnValueHandlers);
        }
        // 将新的处理器列表设置回请求映射处理器适配器
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }
}

package com.gls.athena.starter.word.config;

import com.gls.athena.starter.word.handler.WordResponseHandler;
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
 * @author athena
 */
@Configuration
@EnableConfigurationProperties(WordProperties.class)
public class WordConfig {
    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    @Resource
    private WordResponseHandler wordResponseHandler;

    @PostConstruct
    public void init() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(wordResponseHandler);
        if (returnValueHandlers != null) {
            newHandlers.addAll(returnValueHandlers);
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }
}

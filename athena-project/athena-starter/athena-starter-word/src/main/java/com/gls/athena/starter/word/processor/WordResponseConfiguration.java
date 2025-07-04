package com.gls.athena.starter.word.processor;

import com.gls.athena.starter.word.generator.WordDocumentGeneratorFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Word响应处理器配置
 *
 * @author athena
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WordResponseConfiguration implements WebMvcConfigurer {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    private final WordDocumentGeneratorFactory generatorFactory;

    @Bean
    public WordResponseProcessor wordResponseProcessor() {
        return new WordResponseProcessor(generatorFactory);
    }

    @PostConstruct
    public void init() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(wordResponseProcessor());
        if (returnValueHandlers != null) {
            newHandlers.addAll(returnValueHandlers);
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }
}

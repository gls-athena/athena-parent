package com.gls.athena.starter.word.handler;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordGenerator;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

/**
 * Word响应处理器
 *
 * @author athena
 */
@Component
public class WordResponseHandler implements HandlerMethodReturnValueHandler {


    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(WordResponse.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

    }
}

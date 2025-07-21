package com.gls.athena.starter.word.handler;

import com.gls.athena.starter.web.util.WebUtil;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordGeneratorManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * Word响应处理器
 *
 * @author athena
 */
@Slf4j
@RequiredArgsConstructor
public class WordResponseHandler implements HandlerMethodReturnValueHandler {

    /**
     * Word 生成器管理器。
     */
    private final WordGeneratorManager generatorManager;

    /**
     * 判断方法返回值是否支持 @WordResponse 注解。
     *
     * @param returnType 方法参数信息
     * @return 是否支持 @WordResponse 注解
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(WordResponse.class);
    }

    /**
     * 处理带有 @WordResponse 注解的方法返回值，将数据导出为 Word 文档。
     *
     * @param returnValue  控制器方法返回值
     * @param returnType   方法参数信息
     * @param mavContainer ModelAndView 容器
     * @param webRequest   当前 Web 请求
     * @throws Exception 处理异常
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        // 标记请求已被处理，防止其他处理器继续处理
        mavContainer.setRequestHandled(true);

        // 获取 @WordResponse 注解配置
        WordResponse wordResponse = Optional.ofNullable(returnType.getMethodAnnotation(WordResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("方法返回值必须使用@WordResponse注解标记"));

        // 创建输出流并导出 Word 文件
        try (OutputStream outputStream = WebUtil.createOutputStream(webRequest, wordResponse.filename(), wordResponse.fileType())) {
            generatorManager.generate(returnValue, wordResponse, outputStream);
        } catch (IOException e) {
            log.error("导出 Word 文件时发生错误", e);
            throw e;
        }
    }

}

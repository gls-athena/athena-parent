package com.gls.athena.starter.file.base;

import com.gls.athena.starter.file.generator.FileGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 文件响应处理器抽象基类
 * <p>
 * 该类实现了Spring MVC的HandlerMethodReturnValueHandler接口，
 * 用于处理带有特定注解的控制器方法返回值，将其转换为文件响应。
 *
 * @param <Response> 响应注解类型，必须继承自Annotation
 * @author lizy19
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseFileResponseHandler<Generator extends FileGenerator<Response>, Response extends Annotation>
        implements HandlerMethodReturnValueHandler {

    private final List<Generator> generators;

    /**
     * 判断当前处理器是否支持指定的方法参数类型
     * <p>
     * 通过检查方法上是否存在指定类型的注解来判断是否支持该返回类型
     *
     * @param parameter 方法参数信息，包含方法签名和注解等信息
     * @return 如果方法上有对应的注解且不是异步处理则返回true，否则返回false
     */
    @Override
    public boolean supportsReturnType(MethodParameter parameter) {
        Response response = parameter.getMethodAnnotation(getResponseClass());
        if (response == null) {
            return false;
        }
        BaseFileResponseWrapper<Response> wrapper = getResponseWrapper(response);
        return !wrapper.isAsync();
    }

    /**
     * 处理控制器方法的返回值，生成文件响应
     * <p>
     * 该方法会创建输出流并将返回值通过文件生成器转换为文件内容输出
     *
     * @param returnValue  控制器方法的返回值对象
     * @param returnType   返回值的方法参数信息
     * @param mavContainer ModelAndView容器，用于标记请求已处理
     * @param webRequest   当前的Web请求对象
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 标记请求已被处理，防止其他处理器继续处理
        mavContainer.setRequestHandled(true);

        // 获取Response注解
        Response response = returnType.getMethodAnnotation(getResponseClass());
        BaseFileResponseWrapper<Response> wrapper = getResponseWrapper(response);
        // 创建文件输出流并生成文件
        try (OutputStream outputStream = wrapper.createOutputStream(webRequest)) {
            generators.stream()
                    .filter(generator -> generator.supports(response))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("未找到适配的Generator实现"))
                    .generate(returnValue, response, outputStream);
        } catch (Exception e) {
            log.error("导出文件时发生错误 ：{}", e.getMessage(), e);
        }
    }

    /**
     * 获取响应注解的类型Class
     *
     * @return 响应注解的Class对象
     */
    protected abstract Class<Response> getResponseClass();

    /**
     * 获取响应包装器实例，用于解析响应注解中的配置信息
     *
     * @param response 响应注解对象
     * @return 对应的响应包装器实例
     */
    protected abstract BaseFileResponseWrapper<Response> getResponseWrapper(Response response);
}


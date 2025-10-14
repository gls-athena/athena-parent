package com.gls.athena.starter.file.support;

import cn.hutool.core.util.TypeUtil;
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
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class FileResponseHandler<Generator extends FileGenerator<Response>, Response extends Annotation>
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
        FileResponseWrapper<Response> wrapper = getResponseWrapper(parameter);
        if (wrapper == null) {
            return false;
        }
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
        FileResponseWrapper<Response> wrapper = getResponseWrapper(returnType);
        if (wrapper == null) {
            log.error("无法获取文件响应包装器");
            throw new Exception("无法获取文件响应包装器");
        }

        // 查找合适的生成器
        Generator generator = findSupportedGenerator(wrapper);

        // 创建文件输出流并生成文件
        try (OutputStream outputStream = wrapper.createOutputStream(webRequest)) {
            generator.generate(returnValue, wrapper.getResponse(), outputStream);
            log.debug("文件生成成功: filename={}", wrapper.getFilename());
        } catch (Exception e) {
            log.error("导出文件时发生错误: filename={}", wrapper.getFilename(), e);
            throw e;
        }
    }

    /**
     * 查找支持的文件生成器
     *
     * @param wrapper 响应包装器
     * @return 支持的生成器实例
     */
    private Generator findSupportedGenerator(FileResponseWrapper<Response> wrapper) {
        return generators.stream()
                .filter(generator -> wrapper.isSupport(generator) || generator.supports(wrapper.getResponse()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到适配的Generator实现: " + wrapper.getGenerator().getName()));
    }

    /**
     * 获取响应包装器
     *
     * @param parameter 方法参数对象，用于获取方法上的注解信息
     * @return FileResponseWrapper<Response> 响应包装器对象，如果无法获取到响应类或注解则返回null
     */
    @SuppressWarnings("unchecked")
    private FileResponseWrapper<Response> getResponseWrapper(MethodParameter parameter) {
        // 获取泛型参数中指定索引位置的类型参数
        Class<Response> responseClass = (Class<Response>) TypeUtil.getTypeArgument(this.getClass(), 1);
        if (responseClass == null) {
            return null;
        }
        // 从方法参数中获取指定类型的注解
        Response response = parameter.getMethodAnnotation(responseClass);
        return FileResponseWrapper.of(response);
    }
}

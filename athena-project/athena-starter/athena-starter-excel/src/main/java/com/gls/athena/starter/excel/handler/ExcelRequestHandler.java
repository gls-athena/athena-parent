package com.gls.athena.starter.excel.handler;

import com.alibaba.excel.EasyExcel;
import com.gls.athena.starter.excel.annotation.ExcelRequest;
import com.gls.athena.starter.excel.listener.IReadListener;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Excel文件上传请求参数解析器
 * 实现Spring MVC的参数解析器接口，用于处理带有@ExcelRequest注解的方法参数
 * 主要功能：
 * 1. 接收上传的Excel文件
 * 2. 解析Excel内容并转换为对象列表
 * 3. 处理数据验证结果
 * 4. 支持自定义读取监听器
 *
 * @author george
 */
public class ExcelRequestHandler implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ExcelRequest.class);
    }

    /**
     * 解析上传的Excel文件并转换为对象列表
     *
     * @param parameter     方法参数信息，包含参数类型和注解信息
     * @param mavContainer  Spring MVC的模型视图容器
     * @param webRequest    当前Web请求对象
     * @param binderFactory 数据绑定工厂
     * @return 解析后的对象列表
     * @throws Exception 解析过程中的异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        validateParameterType(parameter);

        Class<?> targetType = getTargetType(parameter);
        ExcelRequest excelRequest = getExcelRequest(parameter);
        IReadListener<?> readListener = createReadListener(excelRequest);

        try (InputStream inputStream = getInputStream(webRequest, excelRequest.fileName())) {
            if (inputStream == null) {
                throw new IllegalArgumentException("未找到上传的Excel文件: " + excelRequest.fileName());
            }

            processExcelFile(inputStream, targetType, readListener, excelRequest);
            bindValidationResult(mavContainer, webRequest, binderFactory, readListener);

            return readListener.getList();
        }
    }

    /**
     * 验证参数类型是否为List类型
     *
     * @param parameter 方法参数信息
     * @throws IllegalArgumentException 当参数类型不是List时抛出
     */
    private void validateParameterType(MethodParameter parameter) {
        if (!List.class.isAssignableFrom(parameter.getParameterType())) {
            throw new IllegalArgumentException(
                    String.format("Excel解析错误：参数类型必须是List，当前类型：%s", parameter.getParameterType().getName())
            );
        }
    }

    /**
     * 处理Excel文件的读取过程
     * 使用EasyExcel框架进行文件解析
     *
     * @param inputStream  Excel文件输入流
     * @param targetType   目标对象类型
     * @param readListener 自定义读取监听器
     * @param excelRequest Excel请求注解信息
     */
    private void processExcelFile(InputStream inputStream, Class<?> targetType,
                                  IReadListener<?> readListener, ExcelRequest excelRequest) {
        EasyExcel.read(inputStream, targetType, readListener)
                .headRowNumber(excelRequest.headRowNumber())
                .ignoreEmptyRow(excelRequest.ignoreEmptyRow())
                .sheet()
                .doRead();
    }

    /**
     * 绑定数据验证结果到Spring MVC的模型中
     *
     * @param mavContainer  模型视图容器
     * @param webRequest    Web请求对象
     * @param binderFactory 数据绑定工厂
     * @param readListener  包含验证错误信息的读取监听器
     * @throws Exception 绑定过程中的异常
     */
    private void bindValidationResult(ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                      WebDataBinderFactory binderFactory, IReadListener<?> readListener) throws Exception {
        WebDataBinder binder = binderFactory.createBinder(webRequest, readListener.getErrors(), "excel");
        mavContainer.getModel().put(BindingResult.MODEL_KEY_PREFIX + "excel", binder.getBindingResult());
    }

    /**
     * 创建Excel读取监听器实例
     *
     * @param excelRequest Excel请求注解
     * @return 读取监听器实例
     * @throws IllegalStateException 创建监听器失败时抛出
     */
    private IReadListener<?> createReadListener(ExcelRequest excelRequest) {
        try {
            return BeanUtils.instantiateClass(excelRequest.readListener());
        } catch (Exception e) {
            throw new IllegalStateException("无法创建Excel读取监听器: " + excelRequest.readListener(), e);
        }
    }

    /**
     * 获取目标类型（List泛型类型）
     *
     * @param parameter 方法参数信息
     * @return List的泛型类型
     */
    private Class<?> getTargetType(MethodParameter parameter) {
        return ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric();
    }

    /**
     * 获取ExcelRequest注解
     *
     * @param parameter 方法参数信息
     * @return Excel请求注解
     * @throws IllegalArgumentException 注解不存在时抛出
     */
    private ExcelRequest getExcelRequest(MethodParameter parameter) {
        ExcelRequest excelRequest = parameter.getParameterAnnotation(ExcelRequest.class);
        if (excelRequest == null) {
            throw new IllegalArgumentException("Excel上传请求解析器错误, @ExcelRequest参数为空");
        }
        return excelRequest;
    }

    /**
     * 获取上传文件的输入流
     *
     * @param webRequest Web请求对象
     * @param fileName   文件参数名
     * @return 文件输入流，如果文件不存在则返回null
     * @throws IOException           读取文件失败时抛出
     * @throws IllegalStateException 请求不是多部分请求时抛出
     */
    private InputStream getInputStream(NativeWebRequest webRequest, String fileName) throws IOException {
        MultipartRequest multipartRequest = webRequest.getNativeRequest(MultipartRequest.class);
        if (multipartRequest == null) {
            throw new IllegalStateException("当前请求不是多部分请求，无法处理文件上传");
        }

        MultipartFile file = multipartRequest.getFile(fileName);
        return file != null ? file.getInputStream() : null;
    }
}

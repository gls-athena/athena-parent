package com.gls.athena.starter.excel.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.idev.excel.FastExcel;
import com.gls.athena.starter.excel.annotation.ExcelRequest;
import com.gls.athena.starter.excel.listener.IReadListener;
import com.gls.athena.starter.excel.support.ExcelErrorMessage;
import lombok.extern.slf4j.Slf4j;
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

import java.io.FileNotFoundException;
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
@Slf4j
public class ExcelRequestHandler implements HandlerMethodArgumentResolver {

    /**
     * 检查方法参数是否支持Excel文件解析
     *
     * <p>该方法用于判断给定的方法参数是否带有{@link ExcelRequest}注解，
     * 从而确定是否需要进行Excel文件解析处理。</p>
     *
     * @param parameter 待检查的方法参数对象，包含参数元数据信息
     * @return 如果参数带有{@link ExcelRequest}注解则返回true，否则返回false
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.debug("解析Excel文件参数：{}", parameter);
        // 核心逻辑：通过检查参数注解判断是否支持该参数类型
        return parameter.hasParameterAnnotation(ExcelRequest.class);
    }

    /**
     * 解析Excel请求参数，将Excel文件内容转换为指定类型的List对象
     *
     * @param parameter     方法参数信息，包含参数类型和注解等元数据
     * @param mavContainer  ModelAndView容器，用于存储模型数据和视图信息
     * @param webRequest    原生Web请求对象，用于获取请求内容
     * @param binderFactory Web数据绑定工厂，用于创建数据绑定器
     * @return 解析后的List对象，包含Excel数据
     * @throws Exception 当参数类型不符合要求、注解缺失、Excel解析失败或IO异常时抛出
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 参数类型校验：必须为List类型
        Class<?> parameterType = parameter.getParameterType();
        if (!List.class.isAssignableFrom(parameterType)) {
            throw new IllegalArgumentException("Excel解析错误：参数类型必须是List，当前类型：" + parameterType.getName());
        }

        // 检查ExcelRequest注解是否存在
        ExcelRequest excelRequest = parameter.getParameterAnnotation(ExcelRequest.class);
        if (excelRequest == null) {
            throw new IllegalArgumentException("Excel解析错误：参数未添加ExcelRequest注解");
        }

        // 获取List的泛型类型并实例化读取监听器
        Class<?> genericType = ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric();
        IReadListener<?> readListener = BeanUtils.instantiateClass(excelRequest.readListener());

        try (InputStream inputStream = getInputStream(webRequest, excelRequest.fileName())) {
            // 使用FastExcel进行Excel解析
            FastExcel.read(inputStream, genericType, readListener)
                    .headRowNumber(excelRequest.headRowNumber())
                    .ignoreEmptyRow(excelRequest.ignoreEmptyRow())
                    .sheet()
                    .doRead();

            // 获取解析结果并校验
            List<?> resultList = readListener.getList();
            if (CollUtil.isEmpty(resultList)) {
                throw new IllegalStateException("Excel解析错误：readListener返回的列表为null");
            }

            // 处理解析过程中产生的错误信息
            List<ExcelErrorMessage> errors = readListener.getErrors();
            if (CollUtil.isNotEmpty(errors)) {
                log.warn("Excel解析过程中发现错误：{}", errors);
                WebDataBinder binder = binderFactory.createBinder(webRequest, errors, "excel");
                mavContainer.getModel().put(BindingResult.MODEL_KEY_PREFIX + "excel", binder.getBindingResult());
            }
            return resultList;
        } catch (IOException e) {
            throw new IllegalStateException("Excel解析错误：无法读取输入流", e);
        }
    }

    /**
     * 从NativeWebRequest中获取指定文件名的输入流
     *
     * @param webRequest 原生Web请求对象，用于获取多部分请求
     * @param fileName   要获取的文件名，不能为空
     * @return 文件的输入流
     * @throws IllegalArgumentException 如果文件名为空
     * @throws IllegalStateException    如果webRequest为空或不是多部分请求
     * @throws FileNotFoundException    如果指定文件名的文件不存在
     * @throws IOException              如果获取输入流时发生I/O错误
     */
    private InputStream getInputStream(NativeWebRequest webRequest, String fileName) throws IOException {
        // 参数验证
        if (StrUtil.isEmpty(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 检查请求是否为多部分请求
        if (webRequest == null) {
            throw new IllegalStateException("请求对象不能为空");
        }

        // 验证并获取多部分请求对象
        MultipartRequest multipartRequest = webRequest.getNativeRequest(MultipartRequest.class);
        if (multipartRequest == null) {
            throw new IllegalStateException("当前请求不是多部分请求，无法处理文件上传");
        }

        // 获取并验证文件对象
        MultipartFile file = multipartRequest.getFile(fileName);
        if (file == null) {
            throw new FileNotFoundException("文件不存在: " + fileName);
        }

        return file.getInputStream();
    }

}

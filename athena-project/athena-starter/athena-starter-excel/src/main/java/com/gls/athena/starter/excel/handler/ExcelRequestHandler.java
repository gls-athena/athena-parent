package com.gls.athena.starter.excel.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.idev.excel.FastExcel;
import com.gls.athena.starter.excel.annotation.ExcelRequest;
import com.gls.athena.starter.excel.factory.ReadListenerFactory;
import com.gls.athena.starter.excel.listener.IReadListener;
import com.gls.athena.starter.excel.support.ExcelErrorMessage;
import lombok.extern.slf4j.Slf4j;
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
 * 优化后的Excel文件上传请求参数解析器
 * <p>
 * 主要优化：
 * 1. 使用工厂模式创建监听器
 * 2. 改进异常处理和日志记录
 * 3. 增强参数校验逻辑
 * 4. 支持更灵活的配置
 *
 * @author george
 */
@Slf4j
public class ExcelRequestHandler implements HandlerMethodArgumentResolver {

    /**
     * 检查方法参数是否支持Excel文件解析
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isSupported = parameter.hasParameterAnnotation(ExcelRequest.class) &&
                List.class.isAssignableFrom(parameter.getParameterType());

        log.debug("检查Excel参数支持: {} -> {}", parameter.getParameterName(), isSupported);
        return isSupported;
    }

    /**
     * 解析Excel请求参数，将Excel文件内容转换为指定类型的List对象
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        log.info("开始解析Excel参数: {}", parameter.getParameterName());

        try {
            // 参数验证
            validateParameter(parameter);

            // 获取注解配置
            ExcelRequest excelRequest = parameter.getParameterAnnotation(ExcelRequest.class);

            // 获取泛型类型
            Class<?> genericType = ResolvableType.forMethodParameter(parameter)
                    .asCollection()
                    .resolveGeneric();

            // 使用工厂创建监听器
            IReadListener<?> readListener = ReadListenerFactory.createListener(excelRequest.readListener());

            // 解析Excel文件
            List<?> resultList = parseExcelFile(webRequest, excelRequest, genericType, readListener);

            // 处理错误信息
            handleValidationErrors(readListener, mavContainer, binderFactory, webRequest);

            log.info("Excel解析完成，数据量: {}, 错误数: {}",
                    resultList.size(), readListener.getErrors().size());

            return resultList;

        } catch (Exception e) {
            log.error("Excel解析失败: {}", e.getMessage(), e);
            throw new IllegalStateException("Excel解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证参数
     */
    private void validateParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        if (!List.class.isAssignableFrom(parameterType)) {
            throw new IllegalArgumentException(
                    String.format("Excel解析错误：参数类型必须是List，当前类型：%s", parameterType.getName()));
        }

        ExcelRequest excelRequest = parameter.getParameterAnnotation(ExcelRequest.class);
        if (excelRequest == null) {
            throw new IllegalArgumentException("Excel解析错误：参数未添加ExcelRequest注解");
        }
    }

    /**
     * 解析Excel文件
     */
    private List<?> parseExcelFile(NativeWebRequest webRequest, ExcelRequest excelRequest,
                                   Class<?> genericType, IReadListener<?> readListener) throws IOException {

        try (InputStream inputStream = getInputStream(webRequest, excelRequest.fileName())) {
            // 使用FastExcel进行Excel解析
            FastExcel.read(inputStream, genericType, readListener)
                    .headRowNumber(excelRequest.headRowNumber())
                    .ignoreEmptyRow(excelRequest.ignoreEmptyRow())
                    .sheet()
                    .doRead();

            List<?> resultList = readListener.getList();
            if (CollUtil.isEmpty(resultList)) {
                log.warn("Excel解析结果为空");
                throw new IllegalStateException("Excel文件内容为空或格式不正确");
            }

            return resultList;
        }
    }

    /**
     * 处理校验错误
     */
    private void handleValidationErrors(IReadListener<?> readListener, ModelAndViewContainer mavContainer,
                                        WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {

        List<ExcelErrorMessage> errors = readListener.getErrors();
        if (CollUtil.isNotEmpty(errors)) {
            log.warn("Excel解析过程中发现 {} 个错误", errors.size());

            // 记录前几个错误用于调试
            errors.stream().limit(5).forEach(error ->
                    log.debug("Excel错误: 行{}, 字段{}, 消息{}", error.getRowIndex(), error.getFieldName(), error.getMessage()));

            WebDataBinder binder = binderFactory.createBinder(webRequest, errors, "excel");
            mavContainer.getModel().put(BindingResult.MODEL_KEY_PREFIX + "excel", binder.getBindingResult());
        }
    }

    /**
     * 从NativeWebRequest中获取指定文件名的输入流
     */
    private InputStream getInputStream(NativeWebRequest webRequest, String fileName) throws IOException {
        // 参数验证
        if (StrUtil.isEmpty(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

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
        if (file == null || file.isEmpty()) {
            throw new FileNotFoundException("文件不存在或为空: " + fileName);
        }

        // 文件大小检查
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB限制
            throw new IllegalArgumentException("文件大小超过限制(10MB): " + file.getSize());
        }

        log.debug("获取Excel文件: {}, 大小: {} bytes", fileName, file.getSize());
        return file.getInputStream();
    }

}

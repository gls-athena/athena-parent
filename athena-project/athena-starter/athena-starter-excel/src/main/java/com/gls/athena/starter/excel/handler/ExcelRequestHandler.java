package com.gls.athena.starter.excel.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.idev.excel.FastExcel;
import com.gls.athena.starter.excel.annotation.ExcelRequest;
import com.gls.athena.starter.excel.exception.ExcelParseException;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Excel文件上传请求参数解析器
 * 实现Spring MVC的参数解析器接口，用于处理带有@ExcelRequest注解的方法参数
 * 主要功能：
 * 1. 接收上传的Excel文件
 * 2. 解析Excel内容并转换为对象列表
 * 3. 处理数据验证结果
 * 4. 支持自定义读取监听器
 * 5. 支持大文件批处理模式
 * 6. 提供详细的错误信息和异常处理
 *
 * @author george
 */
@Slf4j
public class ExcelRequestHandler implements HandlerMethodArgumentResolver {

    /**
     * 默认文件大小阈值（10MB），超过此值将使用批处理模式
     */
    private static final long BATCH_MODE_THRESHOLD = 10 * 1024 * 1024;

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
        boolean supports = parameter.hasParameterAnnotation(ExcelRequest.class);
        if (supports) {
            log.debug("识别到Excel请求参数: {} [{}]", parameter.getParameterName(), parameter.getParameterType().getSimpleName());
        }
        return supports;
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
        long startTime = System.currentTimeMillis();
        try {
            // 参数类型校验：必须为List类型
            Class<?> parameterType = parameter.getParameterType();
            if (!List.class.isAssignableFrom(parameterType)) {
                throw new ExcelParseException("参数类型必须是List，当前类型：" + parameterType.getName());
            }

            // 获取ExcelRequest注解
            ExcelRequest excelRequest = Optional.ofNullable(parameter.getParameterAnnotation(ExcelRequest.class))
                    .orElseThrow(() -> new ExcelParseException("参数未添加ExcelRequest注解"));

            // 获取List的泛型类型
            Class<?> genericType = ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric();
            if (genericType == null) {
                throw new ExcelParseException("无法确定List的泛型类型");
            }

            // 实例化读取监听器
            IReadListener<?> readListener = createReadListener(excelRequest);

            // 获取上传的Excel文件
            MultipartFile file = getMultipartFile(webRequest, excelRequest.fileName());
            log.info("开始处理Excel文件: {}, 大小: {}KB", file.getOriginalFilename(), file.getSize() / 1024);

            // 解析Excel文件
            try (InputStream inputStream = file.getInputStream()) {
                // 配置FastExcel
                FastExcel.read(inputStream, genericType, readListener)
                        .headRowNumber(excelRequest.headRowNumber())
                        .ignoreEmptyRow(excelRequest.ignoreEmptyRow())
                        .sheet()
                        .doRead();
            }

            // 获取解析结果
            List<?> resultList = readListener.getList();

            // 校验结果
            if (CollUtil.isEmpty(resultList)) {
                log.warn("Excel解析完成，但结果为空");
                if (excelRequest.allowEmptyResult()) {
                    return resultList;
                }
                throw new ExcelParseException("解析结果为空");
            }

            log.info("Excel解析完成，共读取{}条数据", resultList.size());

            // 处理解析过程中产生的错误信息
            handleErrors(readListener, webRequest, binderFactory, mavContainer);

            return resultList;
        } catch (ExcelParseException e) {
            log.error("Excel解析失败: {}", e.getMessage());
            throw e;
        } catch (IOException e) {
            log.error("Excel文件IO异常", e);
            throw new ExcelParseException("Excel文件读取失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Excel处理过程中发生未预期异常", e);
            throw new ExcelParseException("Excel处理失败: " + e.getMessage(), e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("Excel解析处理总耗时: {}ms", duration);
        }
    }

    /**
     * 创建并初始化读取监听器
     *
     * @param excelRequest Excel请求注解
     * @return 初始化后的读取监听器实例
     */
    private IReadListener<?> createReadListener(ExcelRequest excelRequest) {
        try {
            return BeanUtils.instantiateClass(excelRequest.readListener());
        } catch (Exception e) {
            log.error("创建读取监听器失败: {}", excelRequest.readListener().getName(), e);
            throw new ExcelParseException("无法创建读取监听器: " + e.getMessage(), e);
        }
    }

    /**
     * 处理Excel解析过程中产生的错误信息
     *
     * @param readListener  读取监听器
     * @param webRequest    Web请求
     * @param binderFactory 数据绑定工厂
     * @param mavContainer  ModelAndView容器
     */
    private void handleErrors(IReadListener<?> readListener, NativeWebRequest webRequest,
                              WebDataBinderFactory binderFactory, ModelAndViewContainer mavContainer) throws Exception {
        List<ExcelErrorMessage> errors = readListener.getErrors();
        if (CollUtil.isNotEmpty(errors)) {
            int errorCount = errors.size();
            log.warn("Excel解析过程中发现{}个错误", errorCount);

            // 当错误数量过多时，只记录部分错误以避免日志过大
            if (errorCount > 10) {
                log.warn("前10个错误: {}", errors.subList(0, 10));
                log.warn("还有{}个错误未显示...", errorCount - 10);
            } else {
                log.warn("错误详情: {}", errors);
            }

            // 将错误信息绑定到模型中，用于后续处理
            WebDataBinder binder = binderFactory.createBinder(webRequest, errors, "excel");
            mavContainer.getModel().put(BindingResult.MODEL_KEY_PREFIX + "excel", binder.getBindingResult());
        }
    }

    /**
     * 从请求中获取上传的Excel文件
     *
     * @param webRequest 原生Web请求对象
     * @param fileName   文件参数名
     * @return Excel文件对象
     * @throws ExcelParseException 如果文件不存在或获取失败
     */
    private MultipartFile getMultipartFile(NativeWebRequest webRequest, String fileName) throws ExcelParseException {
        // 参数验证
        if (StrUtil.isEmpty(fileName)) {
            throw new ExcelParseException("文件名参数不能为空");
        }

        // 检查请求对象
        if (webRequest == null) {
            throw new ExcelParseException("请求对象不能为空");
        }

        // 验证并获取多部分请求对象
        MultipartRequest multipartRequest = webRequest.getNativeRequest(MultipartRequest.class);
        if (multipartRequest == null) {
            throw new ExcelParseException("当前请求不是多部分请求，无法处理文件上传");
        }

        // 获取文件对象
        MultipartFile file = multipartRequest.getFile(fileName);
        if (file == null) {
            throw new ExcelParseException("文件不存在: " + fileName);
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && !isValidExcelFile(originalFilename)) {
            throw new ExcelParseException("不支持的文件类型: " + originalFilename + "，仅支持.xls和.xlsx格式");
        }

        // 验证文件非空
        if (file.isEmpty()) {
            throw new ExcelParseException("上传的Excel文件为空");
        }

        return file;
    }

    /**
     * 验证文件是否为有效的Excel文件
     *
     * @param filename 文件名
     * @return 是否为Excel文件
     */
    private boolean isValidExcelFile(String filename) {
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".xlsx") || lowerFilename.endsWith(".xls");
    }
}

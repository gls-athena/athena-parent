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

import java.io.InputStream;
import java.util.List;

/**
 * Excel文件上传请求参数解析器
 *
 * <p>该类实现了Spring MVC的HandlerMethodArgumentResolver接口，用于处理带有@ExcelRequest注解的方法参数。
 * 主要功能包括：
 * <ul>
 *   <li>解析上传的Excel文件</li>
 *   <li>将Excel数据转换为Java对象列表</li>
 *   <li>处理解析过程中的错误和验证</li>
 *   <li>支持自定义读取监听器</li>
 * </ul>
 *
 * @author Athena Framework
 * @since 1.0.0
 */
@Slf4j
public class ExcelRequestHandler implements HandlerMethodArgumentResolver {

    /**
     * 判断是否支持解析指定的方法参数
     *
     * <p>只有带有@ExcelRequest注解的参数才会被此解析器处理
     *
     * @param parameter 方法参数
     * @return 如果参数带有@ExcelRequest注解则返回true，否则返回false
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ExcelRequest.class);
    }

    /**
     * 解析方法参数，将上传的Excel文件转换为Java对象列表
     *
     * <p>解析流程：
     * <ol>
     *   <li>验证参数类型必须是List类型</li>
     *   <li>获取List的泛型类型</li>
     *   <li>创建读取监听器实例</li>
     *   <li>获取上传的Excel文件</li>
     *   <li>使用FastExcel进行文件解析</li>
     *   <li>处理解析错误和验证结果</li>
     * </ol>
     *
     * @param parameter     方法参数
     * @param mavContainer  ModelAndView容器
     * @param webRequest    Web请求对象
     * @param binderFactory Web数据绑定器工厂
     * @return 解析后的Java对象列表
     * @throws Exception 解析过程中的异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 验证参数类型必须是List
        if (!List.class.isAssignableFrom(parameter.getParameterType())) {
            throw new ExcelParseException("参数类型必须是List");
        }

        // 获取@ExcelRequest注解配置
        ExcelRequest excelRequest = parameter.getParameterAnnotation(ExcelRequest.class);

        // 解析List的泛型类型，即Excel行数据要转换的目标对象类型
        Class<?> genericType = ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric();

        if (genericType == null) {
            throw new ExcelParseException("无法确定List的泛型类型");
        }

        try {
            // 创建读取监听器实例，用于处理Excel读取过程中的数据和错误
            IReadListener<?> readListener = BeanUtils.instantiateClass(excelRequest.readListener());

            // 获取上传的Excel文件
            MultipartFile file = getMultipartFile(webRequest, excelRequest.fileName());

            // 使用FastExcel读取Excel文件
            try (InputStream inputStream = file.getInputStream()) {
                FastExcel.read(inputStream, genericType, readListener)
                        // 设置表头行数
                        .headRowNumber(excelRequest.headRowNumber())
                        // 设置是否忽略空行
                        .ignoreEmptyRow(excelRequest.ignoreEmptyRow())
                        // 选择第一个工作表
                        .sheet()
                        // 执行读取操作
                        .doRead();
            }

            // 获取解析结果
            List<?> resultList = readListener.getList();

            // 检查解析结果是否为空（如果不允许空结果的话）
            if (CollUtil.isEmpty(resultList) && !excelRequest.allowEmptyResult()) {
                throw new ExcelParseException("解析结果为空");
            }

            // 处理解析过程中的错误信息
            handleErrors(readListener, binderFactory, webRequest, mavContainer);
            log.info("Excel解析完成，共读取{}条数据", CollUtil.size(resultList));
            return resultList;

        } catch (ExcelParseException e) {
            // 重新抛出Excel解析异常
            throw e;
        } catch (Exception e) {
            // 包装其他异常为Excel解析异常
            throw new ExcelParseException("Excel处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理Excel解析过程中的错误信息
     *
     * <p>将解析错误添加到Model中，以便在前端页面显示错误信息
     *
     * @param readListener  读取监听器，包含解析过程中收集的错误信息
     * @param binderFactory Web数据绑定器工厂
     * @param webRequest    Web请求对象
     * @param mavContainer  ModelAndView容器
     * @throws Exception 处理过程中的异常
     */
    private void handleErrors(IReadListener<?> readListener, WebDataBinderFactory binderFactory,
                              NativeWebRequest webRequest, ModelAndViewContainer mavContainer) throws Exception {
        List<ExcelErrorMessage> errors = readListener.getErrors();
        if (CollUtil.isEmpty(errors)) {
            return;
        }

        // 记录错误日志，如果错误过多则只显示前10个
        log.warn("Excel解析发现{}个错误{}", errors.size(),
                errors.size() > 10 ? "，仅显示前10个: " + errors.subList(0, 10) : ": " + errors);

        // 创建数据绑定器，将错误信息添加到BindingResult中
        WebDataBinder binder = binderFactory.createBinder(webRequest, errors, "excel");
        mavContainer.getModel().put(BindingResult.MODEL_KEY_PREFIX + "excel", binder.getBindingResult());
    }

    /**
     * 从Web请求中获取指定名称的Excel文件
     *
     * <p>执行以下验证：
     * <ul>
     *   <li>文件名不能为空</li>
     *   <li>请求必须是多部分请求</li>
     *   <li>文件必须存在且不为空</li>
     *   <li>文件格式必须是.xls或.xlsx</li>
     * </ul>
     *
     * @param webRequest Web请求对象
     * @param fileName   文件参数名称
     * @return 上传的Excel文件
     * @throws ExcelParseException 文件获取或验证失败时抛出
     */
    private MultipartFile getMultipartFile(NativeWebRequest webRequest, String fileName) {
        // 验证文件名参数
        if (StrUtil.isEmpty(fileName)) {
            throw new ExcelParseException("文件名参数不能为空");
        }

        // 获取多部分请求对象
        MultipartRequest multipartRequest = webRequest.getNativeRequest(MultipartRequest.class);
        if (multipartRequest == null) {
            throw new ExcelParseException("当前请求不是多部分请求");
        }

        // 获取指定名称的文件
        MultipartFile file = multipartRequest.getFile(fileName);
        if (file == null || file.isEmpty()) {
            throw new ExcelParseException("文件不存在或为空: " + fileName);
        }

        // 验证文件格式
        String filename = file.getOriginalFilename();
        if (filename != null && !filename.toLowerCase().matches(".*\\.(xlsx?|xls)$")) {
            throw new ExcelParseException("不支持的文件类型，仅支持.xls和.xlsx格式");
        }

        return file;
    }
}

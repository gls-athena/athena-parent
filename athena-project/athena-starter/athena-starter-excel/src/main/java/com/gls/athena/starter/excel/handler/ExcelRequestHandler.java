package com.gls.athena.starter.excel.handler;

import com.alibaba.excel.EasyExcel;
import com.gls.athena.starter.excel.annotation.ExcelRequest;
import com.gls.athena.starter.excel.listener.IReadListener;
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

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.debug("解析Excel文件参数：{}", parameter);
        return parameter.hasParameterAnnotation(ExcelRequest.class);
    }

    /**
     * 解析上传的Excel文件并转换为对象列表
     * <p>
     * 该方法用于处理上传的Excel文件，将其解析为指定类型的对象列表。首先会验证参数类型，然后根据参数信息获取目标类型和Excel请求配置。
     * 接着，通过读取Excel文件流，使用指定的读取监听器处理文件内容，并将解析结果绑定到Spring MVC的模型视图容器中。
     * 最终返回解析后的对象列表。
     *
     * @param parameter     方法参数信息，包含参数类型和注解信息
     * @param mavContainer  Spring MVC的模型视图容器，用于存储模型数据和视图信息
     * @param webRequest    当前Web请求对象，用于获取请求相关信息
     * @param binderFactory 数据绑定工厂，用于创建数据绑定器
     * @return 解析后的对象列表
     * @throws Exception 解析过程中的异常，如文件未找到、解析错误等
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 验证参数类型是否合法
        validateParameterType(parameter);

        // 获取目标类型和Excel请求配置
        Class<?> targetType = getTargetType(parameter);
        ExcelRequest excelRequest = getExcelRequest(parameter);
        IReadListener<?> readListener = createReadListener(excelRequest);

        // 获取上传的Excel文件流并处理
        try (InputStream inputStream = getInputStream(webRequest, excelRequest.fileName())) {
            if (inputStream == null) {
                log.error("未找到上传的Excel文件: {}", excelRequest.fileName());
                throw new IllegalArgumentException("未找到上传的Excel文件: " + excelRequest.fileName());
            }

            // 处理Excel文件内容，并将解析结果绑定到模型视图容器中
            processExcelFile(inputStream, targetType, readListener, excelRequest);
            bindValidationResult(mavContainer, webRequest, binderFactory, readListener);

            // 返回解析后的对象列表
            return readListener.getList();
        }
    }

    /**
     * 验证方法参数的类型是否为List类型。
     * <p>
     * 该函数用于检查传入的方法参数是否为List类型。如果参数类型不是List，则抛出IllegalArgumentException异常，
     * 并附带错误信息，说明期望的类型和实际类型。
     *
     * @param parameter 方法参数信息，包含参数的类型等元数据。
     * @throws IllegalArgumentException 当参数类型不是List时抛出，异常信息包含期望的类型和实际类型。
     */
    private void validateParameterType(MethodParameter parameter) {
        // 检查参数类型是否为List或其子类
        if (!List.class.isAssignableFrom(parameter.getParameterType())) {
            // 如果类型不匹配，抛出异常并附带错误信息
            throw new IllegalArgumentException(
                    String.format("Excel解析错误：参数类型必须是List，当前类型：%s", parameter.getParameterType().getName())
            );
        }
    }

    /**
     * 处理Excel文件的读取过程
     * 使用EasyExcel框架进行文件解析，支持自定义读取监听器和Excel请求注解配置。
     * 该方法通过传入的输入流、目标对象类型、读取监听器以及Excel请求注解信息，
     * 配置并执行Excel文件的读取操作。
     *
     * @param inputStream  Excel文件的输入流，用于读取Excel文件内容
     * @param targetType   目标对象类型，用于将Excel数据映射到该类型的对象
     * @param readListener 自定义读取监听器，用于处理读取过程中的事件和数据
     * @param excelRequest Excel请求注解信息，包含读取配置如起始行号、是否忽略空行等
     */
    private void processExcelFile(InputStream inputStream, Class<?> targetType,
                                  IReadListener<?> readListener, ExcelRequest excelRequest) {
        // 使用EasyExcel框架读取Excel文件，并配置读取参数
        EasyExcel.read(inputStream, targetType, readListener)
                .headRowNumber(excelRequest.headRowNumber())
                .ignoreEmptyRow(excelRequest.ignoreEmptyRow())
                .sheet()
                .doRead();
    }

    /**
     * 绑定数据验证结果到Spring MVC的模型中
     * <p>
     * 该函数的主要作用是将数据验证的结果绑定到Spring MVC的模型中，以便在视图层展示验证错误信息。
     * 通过使用`WebDataBinderFactory`创建一个`WebDataBinder`对象，并将验证错误信息绑定到模型中。
     *
     * @param mavContainer  模型视图容器，用于存储模型数据和视图信息
     * @param webRequest    Web请求对象，包含当前请求的相关信息
     * @param binderFactory 数据绑定工厂，用于创建`WebDataBinder`对象
     * @param readListener  包含验证错误信息的读取监听器，提供验证错误信息
     * @throws Exception 如果在绑定过程中发生异常，则抛出该异常
     */
    private void bindValidationResult(ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                      WebDataBinderFactory binderFactory, IReadListener<?> readListener) throws Exception {
        // 创建WebDataBinder对象，用于绑定验证错误信息
        WebDataBinder binder = binderFactory.createBinder(webRequest, readListener.getErrors(), "excel");

        // 将绑定结果存储到模型视图容器中，以便在视图层展示
        mavContainer.getModel().put(BindingResult.MODEL_KEY_PREFIX + "excel", binder.getBindingResult());
    }

    /**
     * 创建Excel读取监听器实例
     * <p>
     * 该方法根据传入的ExcelRequest注解中的readListener类信息，通过反射机制实例化对应的读取监听器。
     * 如果实例化过程中发生异常，将抛出IllegalStateException。
     *
     * @param excelRequest Excel请求注解，包含读取监听器的类信息
     * @return 读取监听器实例，类型为IReadListener<?>
     * @throws IllegalStateException 如果无法创建监听器实例，则抛出此异常
     */
    private IReadListener<?> createReadListener(ExcelRequest excelRequest) {
        try {
            // 通过反射机制实例化读取监听器
            return BeanUtils.instantiateClass(excelRequest.readListener());
        } catch (Exception e) {
            // 如果实例化失败，抛出IllegalStateException异常，包含详细的错误信息
            throw new IllegalStateException("无法创建Excel读取监听器: " + excelRequest.readListener(), e);
        }
    }

    /**
     * 获取目标类型（List泛型类型）
     * <p>
     * 该方法通过解析方法参数的泛型信息，获取List集合的泛型类型。
     *
     * @param parameter 方法参数信息，包含方法参数的元数据，如参数类型、泛型信息等
     * @return List的泛型类型，如果无法解析则返回null
     */
    private Class<?> getTargetType(MethodParameter parameter) {
        // 使用ResolvableType解析方法参数的泛型信息，并获取List的泛型类型
        return ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric();
    }

    /**
     * 获取方法参数上的ExcelRequest注解
     * <p>
     * 该方法用于从给定的方法参数中提取ExcelRequest注解。如果参数上没有该注解，则抛出IllegalArgumentException异常。
     *
     * @param parameter 方法参数信息，包含参数的注解信息
     * @return 返回方法参数上的ExcelRequest注解
     * @throws IllegalArgumentException 如果参数上没有ExcelRequest注解，则抛出此异常
     */
    private ExcelRequest getExcelRequest(MethodParameter parameter) {
        // 从方法参数中获取ExcelRequest注解
        ExcelRequest excelRequest = parameter.getParameterAnnotation(ExcelRequest.class);

        // 如果注解不存在，抛出异常
        if (excelRequest == null) {
            throw new IllegalArgumentException("Excel上传请求解析器错误, @ExcelRequest参数为空");
        }

        // 返回找到的ExcelRequest注解
        return excelRequest;
    }

    /**
     * 获取上传文件的输入流
     * <p>
     * 该方法用于从Web请求中获取指定文件名的文件输入流。首先，它会检查请求是否为多部分请求，
     * 如果不是，则抛出IllegalStateException。如果请求是多部分请求，则尝试获取指定文件名的文件，
     * 并返回其输入流。如果文件不存在，则返回null。
     *
     * @param webRequest Web请求对象，用于获取多部分请求
     * @param fileName   文件参数名，指定要获取的文件
     * @return 文件输入流，如果文件不存在则返回null
     * @throws IOException           读取文件失败时抛出
     * @throws IllegalStateException 请求不是多部分请求时抛出
     */
    private InputStream getInputStream(NativeWebRequest webRequest, String fileName) throws IOException {
        // 检查请求是否为多部分请求
        MultipartRequest multipartRequest = webRequest.getNativeRequest(MultipartRequest.class);
        if (multipartRequest == null) {
            throw new IllegalStateException("当前请求不是多部分请求，无法处理文件上传");
        }

        // 获取指定文件名的文件，并返回其输入流
        MultipartFile file = multipartRequest.getFile(fileName);
        return file != null ? file.getInputStream() : null;
    }

}

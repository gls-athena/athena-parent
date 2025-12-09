package com.gls.athena.starter.file.core.domain;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.common.core.enums.FileTypeEnums;
import com.gls.athena.starter.file.core.annotation.FileResponse;
import com.gls.athena.starter.file.core.generator.FileGenerator;
import com.gls.athena.starter.web.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 文件响应包装类，封装了与文件导出相关的元数据及行为。
 * 包含文件编码、名称、描述、文件名、文件类型等属性，并提供判断支持性以及创建输出流的方法。
 *
 * @param <Response> 响应注解的泛型类型
 * @author george
 */
@Slf4j
public record FileResponseWrapper<Response extends Annotation>(String code, String name, String description,
                                                               String filename, FileTypeEnums fileType, boolean async,
                                                               Response response,
                                                               Class<? extends FileGenerator<?>> generator) {

    /**
     * 构造函数初始化逻辑：
     * 对字段进行默认值填充或校验，确保必要字段不为空。
     * 若code/name/description/filename为空则赋予默认值；
     * 若fileType为null则使用默认类型；
     * 若response或generator为空则抛出异常。
     */
    public FileResponseWrapper {
        if (StrUtil.isBlank(code)) {
            code = "FILE_EXPORT";
        }
        if (StrUtil.isBlank(name)) {
            name = "文件导出";
        }
        if (StrUtil.isBlank(description)) {
            description = "文件导出响应";
        }
        if (StrUtil.isBlank(filename)) {
            filename = "exported_file";
        }
        if (fileType == null) {
            fileType = FileTypeEnums.DEFAULT;
        }
        if (response == null) {
            throw new IllegalArgumentException("导出响应注解不能为空");
        }
        if (generator == null) {
            throw new IllegalArgumentException("文件生成器类不能为空");
        }
    }

    /**
     * 根据方法上的注解构建FileResponseWrapper实例。
     * 查找带有FileResponse注解的方法并提取相关信息构造包装对象。
     *
     * @param method 目标方法
     * @return FileResponseWrapper实例，若未找到对应注解则返回null
     */
    public static FileResponseWrapper<?> withMethod(Method method) {
        FileResponse response = AnnotatedElementUtils.getMergedAnnotation(method, FileResponse.class);
        if (response == null) {
            return null;
        }
        // 遍历所有注解以查找目标响应注解
        for (Annotation annotation : method.getAnnotations()) {
            if (AnnotatedElementUtils.isAnnotated(annotation.annotationType(), FileResponse.class)) {
                return new FileResponseWrapper<>(response.code(),
                        response.name(),
                        response.description(),
                        response.filename(),
                        response.fileType(),
                        response.async(),
                        annotation,
                        response.generator());
            }
        }
        return null;
    }

    /**
     * 判断当前响应是否支持指定文件生成器
     *
     * @param generator 文件生成器对象
     * @return true 表示支持该生成器，false 表示不支持
     */
    public boolean supports(FileGenerator<Response> generator) {
        if (generator == null) {
            return false;
        }
        return ObjUtil.equal(generator.getClass(), this.generator);
    }

    /**
     * 创建文件输出流
     * 根据Web请求上下文和文件信息创建输出流，用于文件下载
     *
     * @param webRequest Web请求上下文对象
     * @return 文件输出流
     * @throws IOException IO异常
     */
    public OutputStream createOutputStream(NativeWebRequest webRequest) throws IOException {
        return WebUtil.createOutputStream(webRequest, filename, fileType);
    }
}

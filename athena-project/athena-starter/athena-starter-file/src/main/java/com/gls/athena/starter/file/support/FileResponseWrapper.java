package com.gls.athena.starter.file.support;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.file.generator.FileGenerator;
import com.gls.athena.starter.web.util.WebUtil;
import lombok.Data;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

/**
 * 文件响应包装器抽象类
 * 用于处理文件下载响应的通用包装器，提供文件相关信息的抽象方法
 *
 * @param <Response> 响应注解类型
 * @author george
 */
@Data
public class FileResponseWrapper<Response extends Annotation> {

    /**
     * 响应码
     */
    private final String code;

    /**
     * 名称
     */
    private final String name;

    /**
     * 描述信息
     */
    private final String description;

    /**
     * 文件名
     */
    private final String filename;

    /**
     * 文件类型枚举
     */
    private final FileTypeEnums fileType;

    /**
     * 是否异步处理
     */
    private final boolean async;

    /**
     * 响应注解对象
     */
    private final Response response;

    /**
     * 文件生成器类
     */
    private final Class<? extends FileGenerator<Response>> generator;

    /**
     * 构造函数：通过反射方式从响应注解中提取相关属性并初始化包装器
     *
     * @param response 响应注解对象
     * @throws IllegalArgumentException 如果注解缺少必需的方法
     */
    private FileResponseWrapper(Response response) {
        if (response == null) {
            throw new IllegalArgumentException("响应注解对象不能为空");
        }

        // 使用反射获取注解中的各个字段值，并赋值给当前实例变量
        this.response = response;
        this.code = getAnnotationValue(response, "code", String.class, "");
        this.name = getAnnotationValue(response, "name", String.class, "");
        this.description = getAnnotationValue(response, "description", String.class, "");
        this.filename = getAnnotationValue(response, "filename", String.class, "");
        this.fileType = getAnnotationValue(response, "fileType", FileTypeEnums.class, FileTypeEnums.XLSX);
        this.async = getAnnotationValue(response, "async", Boolean.class, false);
        this.generator = getAnnotationValueUnchecked(response, "generator");

        // 验证必需的字段
        validateRequiredFields();
    }

    /**
     * 静态工厂方法：根据传入的响应注解创建一个 FileResponseWrapper 实例
     *
     * @param response   响应注解对象
     * @param <Response> 注解类型，必须是 Annotation 的子类
     * @return 返回一个新的 FileResponseWrapper 实例；如果输入为 null，则返回 null
     */
    public static <Response extends Annotation> FileResponseWrapper<Response> of(Response response) {
        if (response == null) {
            return null;
        }
        return new FileResponseWrapper<>(response);
    }

    /**
     * 通过反射从注解中获取属性值，带默认值和类型检查
     *
     * @param annotation   注解对象
     * @param methodName   方法名
     * @param returnType   返回类型
     * @param defaultValue 默认值
     * @param <T>          返回值类型
     * @return 属性值或默认值
     */
    private <T> T getAnnotationValue(Response annotation, String methodName, Class<T> returnType, T defaultValue) {
        try {
            Object value = ReflectUtil.invoke(annotation, methodName);
            if (value == null) {
                return defaultValue;
            }
            return returnType.cast(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取生成器类型（未检查的类型转换）
     *
     * @param annotation 注解对象
     * @param methodName 方法名
     * @return 生成器类型
     */
    @SuppressWarnings("unchecked")
    private Class<? extends FileGenerator<Response>> getAnnotationValueUnchecked(Response annotation, String methodName) {
        try {
            Object value = ReflectUtil.invoke(annotation, methodName);
            return (Class<? extends FileGenerator<Response>>) value;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证必需字段
     */
    private void validateRequiredFields() {
        if (StrUtil.isBlank(filename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("文件类型不能为空");
        }
        if (generator == null) {
            throw new IllegalArgumentException("文件生成器不能为空");
        }
    }

    /**
     * 判断当前响应是否支持指定文件生成器
     *
     * @param generator 文件生成器对象
     * @return true 表示支持该生成器，false 表示不支持
     */
    public boolean isSupport(FileGenerator<Response> generator) {
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

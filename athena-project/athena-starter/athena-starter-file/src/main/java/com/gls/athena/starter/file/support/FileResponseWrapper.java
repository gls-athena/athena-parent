package com.gls.athena.starter.file.support;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
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
     */
    public FileResponseWrapper(Response response) {
        this.response = response;
        this.code = ReflectUtil.invoke(response, "code");
        this.name = ReflectUtil.invoke(response, "name");
        this.description = ReflectUtil.invoke(response, "description");
        this.filename = ReflectUtil.invoke(response, "filename");
        this.fileType = ReflectUtil.invoke(response, "fileType");
        this.async = ReflectUtil.invoke(response, "async");
        this.generator = ReflectUtil.invoke(response, "generator");
    }

    /**
     * 判断当前响应是否支持指定文件生成器
     *
     * @param generator 文件生成器对象
     * @return true 表示支持，false 表示不支持
     */
    public boolean isSupport(FileGenerator<Response> generator) {
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

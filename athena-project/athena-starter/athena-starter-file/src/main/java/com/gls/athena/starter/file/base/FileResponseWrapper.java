package com.gls.athena.starter.file.base;

import cn.hutool.core.util.ReflectUtil;
import com.gls.athena.common.core.constant.FileTypeEnums;
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

    private final Response response;

    /**
     * 获取任务编码
     *
     * @return 任务编码字符串
     */
    public String getCode() {
        return ReflectUtil.invoke(response, "code");
    }

    /**
     * 获取任务名称
     *
     * @return 任务名称字符串
     */
    public String getName() {
        return ReflectUtil.invoke(response, "name");
    }

    /**
     * 获取任务描述
     *
     * @return 任务描述字符串
     */
    public String getDescription() {
        return ReflectUtil.invoke(response, "description");
    }

    /**
     * 根据响应注解获取文件名
     *
     * @return 文件名字符串
     */
    public String getFilename() {
        return ReflectUtil.invoke(response, "filename");
    }

    /**
     * 根据响应注解获取文件类型枚举
     *
     * @return 文件类型枚举
     */
    public FileTypeEnums getFileType() {
        return ReflectUtil.invoke(response, "fileType");
    }

    /**
     * 判断当前响应是否为异步处理模式
     *
     * @return true 表示需要异步处理，false 表示同步处理
     */
    public boolean isAsync() {
        return ReflectUtil.invoke(response, "async");
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
        return WebUtil.createOutputStream(webRequest, getFilename(), getFileType());
    }
}


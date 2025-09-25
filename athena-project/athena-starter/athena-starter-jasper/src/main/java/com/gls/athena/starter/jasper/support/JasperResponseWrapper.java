package com.gls.athena.starter.jasper.support;

import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.file.base.BaseFileResponseWrapper;
import com.gls.athena.starter.jasper.annotation.JasperResponse;

/**
 * Jasper响应包装器类，用于封装Jasper报表响应信息
 * 继承自BaseFileResponseWrapper，提供对JasperResponse注解的解析和访问功能
 *
 * @author lizy19
 */
public class JasperResponseWrapper extends BaseFileResponseWrapper<JasperResponse> {

    /**
     * 构造函数，使用指定的JasperResponse注解创建包装器实例
     *
     * @param jasperResponse Jasper响应注解对象，不能为空
     */
    public JasperResponseWrapper(JasperResponse jasperResponse) {
        super(jasperResponse);
    }

    /**
     * 获取响应代码
     *
     * @return 响应代码字符串
     */
    @Override
    public String getCode() {
        return getResponse().code();
    }

    /**
     * 获取响应名称
     *
     * @return 响应名称字符串
     */
    @Override
    public String getName() {
        return getResponse().name();
    }

    /**
     * 获取响应描述信息
     *
     * @return 响应描述字符串
     */
    @Override
    public String getDescription() {
        return getResponse().description();
    }

    /**
     * 获取文件名，包含文件扩展名
     *
     * @return 完整的文件名字符串
     */
    @Override
    public String getFilename() {
        return getResponse().filename() + getFileType().getExtension();
    }

    /**
     * 获取文件类型枚举
     *
     * @return 文件类型枚举值
     */
    @Override
    public FileTypeEnums getFileType() {
        return getResponse().fileType();
    }

    /**
     * 判断是否为异步处理
     *
     * @return true表示异步处理，false表示同步处理
     */
    @Override
    public boolean isAsync() {
        return getResponse().async();
    }
}


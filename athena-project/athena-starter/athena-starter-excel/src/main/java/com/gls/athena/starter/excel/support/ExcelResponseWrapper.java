package com.gls.athena.starter.excel.support;

import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.file.base.BaseFileResponseWrapper;

/**
 * Excel响应包装器类，用于处理Excel响应注解的相关信息
 * 继承自BaseFileResponseWrapper基类，专门处理ExcelResponse注解
 *
 * @author lizy19
 */
public class ExcelResponseWrapper extends BaseFileResponseWrapper<ExcelResponse> {

    /**
     * 构造函数，使用ExcelResponse注解创建包装器实例
     *
     * @param excelResponse Excel响应注解对象，用于获取Excel相关的配置信息
     */
    public ExcelResponseWrapper(ExcelResponse excelResponse) {
        super(excelResponse);
    }

    /**
     * 获取Excel响应的代码标识
     *
     * @return 返回Excel响应注解中配置的代码值
     */
    @Override
    public String getCode() {
        return getResponse().code();
    }

    /**
     * 获取Excel响应的名称
     *
     * @return 返回Excel响应注解中配置的名称
     */
    @Override
    public String getName() {
        return getResponse().name();
    }

    /**
     * 获取Excel响应的描述信息
     *
     * @return 返回Excel响应注解中配置的描述内容
     */
    @Override
    public String getDescription() {
        return getResponse().description();
    }

    /**
     * 获取Excel文件名，包含文件扩展名
     *
     * @return 返回完整的Excel文件名，格式为：文件名+扩展名
     */
    @Override
    public String getFilename() {
        return getResponse().filename() + getResponse().excelType().getValue();
    }

    /**
     * 获取文件类型枚举
     *
     * @return 返回根据Excel类型确定的文件类型枚举值
     */
    @Override
    public FileTypeEnums getFileType() {
        return FileTypeEnums.getFileEnums(getResponse().excelType().getValue());
    }

    /**
     * 判断是否为异步处理
     *
     * @return 返回Excel响应注解中配置的异步标志
     */
    @Override
    public boolean isAsync() {
        return getResponse().async();
    }

}


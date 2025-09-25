package com.gls.athena.starter.word.support;

import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.file.base.BaseFileResponseWrapper;
import com.gls.athena.starter.word.annotation.WordResponse;

/**
 * Word响应包装器类，用于处理Word文件响应的相关信息
 * 继承自BaseFileResponseWrapper基类，专门处理WordResponse注解
 *
 * @author george
 */
public class WordResponseWrapper extends BaseFileResponseWrapper<WordResponse> {

    /**
     * 构造函数，使用WordResponse注解创建包装器实例
     *
     * @param wordResponse Word响应注解对象，用于获取响应配置信息
     */
    public WordResponseWrapper(WordResponse wordResponse) {
        super(wordResponse);
    }

    /**
     * 获取响应代码
     *
     * @return 返回Word响应注解中配置的代码值
     */
    @Override
    public String getCode() {
        return getResponse().code();
    }

    /**
     * 获取响应名称
     *
     * @return 返回Word响应注解中配置的名称值
     */
    @Override
    public String getName() {
        return getResponse().name();
    }

    /**
     * 获取响应描述信息
     *
     * @return 返回Word响应注解中配置的描述信息
     */
    @Override
    public String getDescription() {
        return getResponse().description();
    }

    /**
     * 获取文件名，包含文件扩展名
     *
     * @return 返回完整的文件名，格式为：配置的文件名+文件类型扩展名
     */
    @Override
    public String getFilename() {
        return getResponse().filename() + getFileType().getExtension();
    }

    /**
     * 获取文件类型枚举
     *
     * @return 返回Word响应注解中配置的文件类型枚举值
     */
    @Override
    public FileTypeEnums getFileType() {
        return getResponse().fileType();
    }

    /**
     * 判断是否为异步处理
     *
     * @return 返回Word响应注解中配置的异步处理标志
     */
    @Override
    public boolean isAsync() {
        return getResponse().async();
    }
}


package com.gls.athena.starter.pdf.support;

import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.file.base.BaseFileResponseWrapper;
import com.gls.athena.starter.pdf.annotation.PdfResponse;

/**
 * PDF响应包装器类，用于处理PDF文件响应的相关信息
 * 继承自BaseFileResponseWrapper基类，专门处理PdfResponse注解
 *
 * @author lizy19
 */
public class PdfResponseWrapper extends BaseFileResponseWrapper<PdfResponse> {

    /**
     * 构造函数，创建PDF响应包装器实例
     *
     * @param pdfResponse PDF响应注解对象，用于获取PDF文件的相关配置信息
     */
    public PdfResponseWrapper(PdfResponse pdfResponse) {
        super(pdfResponse);
    }

    /**
     * 获取响应代码
     *
     * @return 返回PDF响应配置中的代码值
     */
    @Override
    public String getCode() {
        return getResponse().code();
    }

    /**
     * 获取响应名称
     *
     * @return 返回PDF响应配置中的名称值
     */
    @Override
    public String getName() {
        return getResponse().name();
    }

    /**
     * 获取响应描述
     *
     * @return 返回PDF响应配置中的描述信息
     */
    @Override
    public String getDescription() {
        return getResponse().description();
    }

    /**
     * 获取文件名，自动添加PDF扩展名
     *
     * @return 返回完整的PDF文件名，包含.pdf扩展名
     */
    @Override
    public String getFilename() {
        return getResponse().filename() + FileTypeEnums.PDF.getExtension();
    }

    /**
     * 获取文件类型枚举
     *
     * @return 返回PDF文件类型枚举值
     */
    @Override
    public FileTypeEnums getFileType() {
        return FileTypeEnums.PDF;
    }

    /**
     * 判断是否为异步处理
     *
     * @return 返回PDF响应配置中的异步处理标志
     */
    @Override
    public boolean isAsync() {
        return getResponse().async();
    }
}


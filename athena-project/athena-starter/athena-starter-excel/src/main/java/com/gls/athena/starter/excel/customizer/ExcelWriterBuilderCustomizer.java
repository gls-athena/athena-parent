package com.gls.athena.starter.excel.customizer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.gls.athena.starter.excel.annotation.ExcelResponse;

import java.nio.charset.Charset;

/**
 * Excel写入构建器自定义器
 * 用于配置和定制化Excel写入过程中的各项参数
 *
 * @author george
 * @since 1.0.0
 */
public class ExcelWriterBuilderCustomizer extends ExcelWriterParameterBuilderCustomizer<ExcelWriterBuilder> {

    /**
     * Excel响应配置
     */
    private final ExcelResponse excelResponse;

    /**
     * 构造自定义器实例
     *
     * @param excelResponse Excel响应配置注解
     */
    public ExcelWriterBuilderCustomizer(ExcelResponse excelResponse) {
        super(excelResponse.parameter());
        this.excelResponse = excelResponse;
    }

    /**
     * 执行Excel写入构建器的定制化配置
     *
     * @param builder Excel写入构建器
     */
    @Override
    public void customize(ExcelWriterBuilder builder) {
        super.customize(builder);

        // 基础配置
        builder.autoCloseStream(excelResponse.autoCloseStream())
                .inMemory(excelResponse.inMemory())
                .writeExcelOnException(excelResponse.writeExcelOnException());

        // 密码保护配置
        if (StrUtil.isNotEmpty(excelResponse.password())) {
            builder.password(excelResponse.password());
        }

        // Excel类型配置
        if (excelResponse.excelType() != null) {
            builder.excelType(excelResponse.excelType());
        }

        // CSV相关配置
        if (StrUtil.isNotEmpty(excelResponse.charset())) {
            builder.charset(Charset.forName(excelResponse.charset()));
        }
        builder.withBom(excelResponse.withBom());

        // 模板配置
        if (StrUtil.isNotEmpty(excelResponse.template())) {
            builder.withTemplate(excelResponse.template());
        }
    }
}

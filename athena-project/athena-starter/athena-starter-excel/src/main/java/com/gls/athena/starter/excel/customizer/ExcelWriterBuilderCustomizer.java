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
     * <p>
     * 该方法用于对Excel写入构建器进行定制化配置，包括基础配置、密码保护配置、Excel类型配置、CSV相关配置以及模板配置。
     * 该方法首先调用父类的customize方法进行基础配置，然后根据excelResponse中的属性值对ExcelWriterBuilder进行进一步的配置。
     *
     * @param builder Excel写入构建器，用于配置Excel写入的相关参数
     */
    @Override
    public void customize(ExcelWriterBuilder builder) {
        super.customize(builder);

        // 基础配置：设置自动关闭流、内存模式、异常时是否写入Excel
        builder.autoCloseStream(excelResponse.autoCloseStream())
                .inMemory(excelResponse.inMemory())
                .writeExcelOnException(excelResponse.writeExcelOnException());

        // 密码保护配置：如果设置了密码，则对Excel文件进行密码保护
        if (StrUtil.isNotEmpty(excelResponse.password())) {
            builder.password(excelResponse.password());
        }

        // Excel类型配置：如果指定了Excel类型，则设置Excel文件的类型
        if (excelResponse.excelType() != null) {
            builder.excelType(excelResponse.excelType());
        }

        // CSV相关配置：如果指定了字符集，则设置CSV文件的字符集，并设置是否包含BOM
        if (StrUtil.isNotEmpty(excelResponse.charset())) {
            builder.charset(Charset.forName(excelResponse.charset()));
        }
        builder.withBom(excelResponse.withBom());

        // 模板配置：如果指定了模板文件路径，则设置Excel文件的模板
        if (StrUtil.isNotEmpty(excelResponse.template())) {
            builder.withTemplate(excelResponse.template());
        }
    }

}

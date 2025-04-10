package com.gls.athena.starter.excel.customizer;

import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.gls.athena.starter.excel.annotation.ExcelSheet;

/**
 * Excel写入Sheet构建器自定义器
 * 用于自定义配置Excel写入时的Sheet相关参数
 *
 * @author george
 * @since 1.0.0
 */
public class ExcelWriterSheetBuilderCustomizer extends ExcelWriterParameterBuilderCustomizer<ExcelWriterSheetBuilder> {

    /**
     * Sheet配置注解
     * 包含sheet相关的配置信息，如sheet编号、名称等
     */
    private final ExcelSheet excelSheet;

    /**
     * 构造Excel Sheet构建器自定义器
     *
     * @param excelSheet Sheet配置注解，不能为null
     */
    public ExcelWriterSheetBuilderCustomizer(final ExcelSheet excelSheet) {
        super(excelSheet.parameter());
        this.excelSheet = excelSheet;
    }

    /**
     * 自定义配置Sheet构建器
     * 该方法用于设置Excel工作表的编号和名称等参数。
     * 首先调用父类的customize方法进行基础配置，然后通过传入的Sheet构建器实例设置工作表的编号和名称。
     *
     * @param builder ExcelWriterSheetBuilder实例，用于构建和配置Excel工作表
     */
    @Override
    public void customize(final ExcelWriterSheetBuilder builder) {
        // 调用父类的customize方法进行基础配置
        super.customize(builder);

        // 设置工作表的编号和名称
        builder.sheetNo(excelSheet.sheetNo())
                .sheetName(excelSheet.sheetName());
    }

}

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
     * 设置sheet编号和名称等参数
     *
     * @param builder Sheet构建器实例
     */
    @Override
    public void customize(final ExcelWriterSheetBuilder builder) {
        super.customize(builder);
        builder.sheetNo(excelSheet.sheetNo())
                .sheetName(excelSheet.sheetName());
    }
}

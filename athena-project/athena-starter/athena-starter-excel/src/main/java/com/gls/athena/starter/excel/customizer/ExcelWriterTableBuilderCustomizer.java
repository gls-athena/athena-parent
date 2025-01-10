package com.gls.athena.starter.excel.customizer;

import com.alibaba.excel.write.builder.ExcelWriterTableBuilder;
import com.gls.athena.starter.excel.annotation.ExcelTable;

/**
 * Excel表格写入构建器的自定义配置类
 * 用于定制Excel表格写入时的Table相关参数
 *
 * @author george
 */
public class ExcelWriterTableBuilderCustomizer extends ExcelWriterParameterBuilderCustomizer<ExcelWriterTableBuilder> {

    private final ExcelTable excelTable;

    /**
     * 初始化表格构建器自定义配置
     *
     * @param excelTable Excel表格注解配置
     */
    public ExcelWriterTableBuilderCustomizer(ExcelTable excelTable) {
        super(excelTable.parameter());
        this.excelTable = excelTable;
    }

    /**
     * 执行自定义配置
     * 设置表格序号等参数
     *
     * @param builder Excel表格构建器
     */
    @Override
    public void customize(ExcelWriterTableBuilder builder) {
        super.customize(builder);
        builder.tableNo(excelTable.tableNo());
    }
}

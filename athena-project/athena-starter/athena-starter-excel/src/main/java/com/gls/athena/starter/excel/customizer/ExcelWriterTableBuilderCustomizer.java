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
     * 该方法用于对Excel表格构建器进行自定义配置，主要设置表格序号等参数。
     * 首先调用父类的customize方法进行基础配置，然后设置表格的序号。
     *
     * @param builder Excel表格构建器，用于构建和配置Excel表格
     */
    @Override
    public void customize(ExcelWriterTableBuilder builder) {
        // 调用父类的customize方法进行基础配置
        super.customize(builder);
        // 设置表格的序号
        builder.tableNo(excelTable.tableNo());
    }

}

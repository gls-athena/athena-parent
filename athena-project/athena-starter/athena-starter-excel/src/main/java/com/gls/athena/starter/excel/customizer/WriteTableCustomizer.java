package com.gls.athena.starter.excel.customizer;

import cn.idev.excel.write.builder.ExcelWriterTableBuilder;
import cn.idev.excel.write.metadata.WriteTable;
import com.gls.athena.starter.excel.annotation.ExcelTable;

/**
 * Excel表格写入构建器的自定义配置类
 * 用于定制Excel表格写入时的Table相关参数
 *
 * @author george
 */
public class WriteTableCustomizer extends BaseWriterCustomizer<ExcelWriterTableBuilder> {

    private final ExcelTable table;

    /**
     * 初始化表格构建器自定义配置
     *
     * @param table Excel表格注解配置
     */
    private WriteTableCustomizer(ExcelTable table) {
        super(table.config());
        this.table = table;
    }

    /**
     * 构建WriteTable对象
     * <p>
     * 该方法通过ExcelWriterTableBuilder和WriteTableBuilderCustomizer，将ExcelTable配置转换为WriteTable对象。
     *
     * @param excelTable 需要转换的Excel表格数据对象，包含表格的配置和数据信息
     * @return WriteTable 构建完成的WriteTable对象，可用于后续的写入操作
     */
    public static WriteTable build(ExcelTable excelTable) {
        // 初始化构建器并进行自定义配置
        ExcelWriterTableBuilder builder = new ExcelWriterTableBuilder();
        WriteTableCustomizer customizer = new WriteTableCustomizer(excelTable);
        customizer.customize(builder);

        return builder.build();
    }

    /**
     * 执行自定义配置
     * 该方法用于对Excel表格构建器进行自定义配置，主要设置表格序号等参数。
     * 首先调用父类的customize方法进行基础配置，然后设置表格的序号。
     *
     * @param builder Excel表格构建器，用于构建和配置Excel表格
     */
    @Override
    public void configure(ExcelWriterTableBuilder builder) {
        // 设置表格的序号
        builder.tableNo(table.tableNo());
    }

}

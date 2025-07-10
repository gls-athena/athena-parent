package com.gls.athena.starter.excel.customizer;

import cn.hutool.core.util.ObjUtil;
import cn.idev.excel.write.metadata.WriteTable;
import com.gls.athena.starter.excel.annotation.ExcelTable;

import java.util.List;

/**
 * Excel表格写入转换器
 * <p>
 * 用于将ExcelTable注解配置转换为EasyExcel的WriteTable参数对象。
 * WriteTable主要用于在单个Excel工作表中创建多个表格区域，
 * 每个表格可以有独立的配置和数据源。
 * </p>
 *
 * @author athena-starter-excel
 * @since 1.0.0
 */
public class WriteTableCustomizer extends BaseWriteCustomizer<WriteTable> {

    private final ExcelTable excelTable;

    private WriteTableCustomizer(ExcelTable excelTable) {
        super(excelTable.config());
        this.excelTable = excelTable;
    }

    public static WriteTable getWriteTable(ExcelTable excelTable) {
        WriteTable writeTable = new WriteTable();
        WriteTableCustomizer writeTableCustomizer = new WriteTableCustomizer(excelTable);
        writeTableCustomizer.customize(writeTable);
        return writeTable;
    }

    public static List<WriteTable> getWriteTables(ExcelTable... tables) {
        return getWriteTables(List.of(tables));
    }

    public static List<WriteTable> getWriteTables(List<ExcelTable> tables) {
        return tables.stream().map(WriteTableCustomizer::getWriteTable).toList();
    }

    @Override
    protected void customizeWrite(WriteTable writeTable) {
        // 设置表格编号，用于标识工作表中的不同表格区域
        if (ObjUtil.isNotEmpty(excelTable.tableNo())) {
            writeTable.setTableNo(excelTable.tableNo());
        }
    }
}

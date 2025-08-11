package com.gls.athena.starter.excel.customizer;

import cn.hutool.core.util.ObjUtil;
import cn.idev.excel.write.metadata.WriteTable;
import com.gls.athena.starter.excel.annotation.ExcelTable;

import java.util.List;

/**
 * WriteTableCustomizer类继承自BaseWriteCustomizer，专门用于定制WriteTable对象
 * 它通过ExcelTable注解的属性来配置WriteTable对象，以满足特定的Excel写入需求
 *
 * @author george
 */
public class WriteTableCustomizer extends BaseWriteCustomizer<WriteTable> {

    /**
     * 保存ExcelTable注解的引用，以便于访问其属性
     */
    private final ExcelTable excelTable;

    /**
     * 私有构造函数，初始化WriteTableCustomizer实例
     *
     * @param excelTable ExcelTable注解，其属性用于定制WriteTable
     */
    private WriteTableCustomizer(ExcelTable excelTable) {
        super(excelTable.config());
        this.excelTable = excelTable;
    }

    /**
     * 创建并返回一个根据ExcelTable注解配置的WriteTable对象
     *
     * @param excelTable ExcelTable注解，其属性用于定制WriteTable
     * @return 定制后的WriteTable对象
     */
    public static WriteTable getWriteTable(ExcelTable excelTable) {
        // 创建WriteTable实例
        WriteTable writeTable = new WriteTable();
        // 创建自定义器并应用ExcelTable注解配置
        WriteTableCustomizer writeTableCustomizer = new WriteTableCustomizer(excelTable);
        writeTableCustomizer.customize(writeTable);
        return writeTable;
    }

    /**
     * 根据多个ExcelTable注解创建并返回一个WriteTable对象列表
     * 此方法重载了另一个getWriteTables方法，允许接受不定数量的ExcelTable参数
     *
     * @param tables 一个或多个ExcelTable注解，每个都将被转换为WriteTable对象
     * @return 包含所有定制后的WriteTable对象的列表
     */
    public static List<WriteTable> getWriteTables(ExcelTable... tables) {
        // 将可变参数数组转换为List，并调用重载方法进行处理
        return getWriteTables(List.of(tables));
    }

    /**
     * 根据ExcelTable注解列表创建并返回一个WriteTable对象列表
     *
     * @param tables 包含多个ExcelTable注解的列表，每个都将被转换为WriteTable对象
     * @return 包含所有定制后的WriteTable对象的列表
     */
    public static List<WriteTable> getWriteTables(List<ExcelTable> tables) {
        // 将ExcelTable注解列表转换为WriteTable对象列表
        return tables.stream().map(WriteTableCustomizer::getWriteTable).toList();
    }

    /**
     * 定制WriteTable对象的写入行为
     * 主要负责根据ExcelTable注解的属性来设置WriteTable对象的属性
     *
     * @param writeTable 待定制的WriteTable对象
     */
    @Override
    protected void customizeWrite(WriteTable writeTable) {
        // 设置表格编号，用于标识工作表中的不同表格区域
        if (ObjUtil.isNotEmpty(excelTable.tableNo())) {
            writeTable.setTableNo(excelTable.tableNo());
        }
    }

}

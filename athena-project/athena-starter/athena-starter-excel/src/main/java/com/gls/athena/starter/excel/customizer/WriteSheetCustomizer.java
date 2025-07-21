package com.gls.athena.starter.excel.customizer;

import cn.hutool.core.util.ObjUtil;
import cn.idev.excel.write.metadata.WriteSheet;
import com.gls.athena.starter.excel.annotation.ExcelSheet;

import java.util.List;

/**
 * 自定义WriteSheet类，用于配置Excel工作表的写入属性
 * 该类继承自BaseWriteCustomizer，针对WriteSheet对象进行定制化配置
 *
 * @author george
 */
public class WriteSheetCustomizer extends BaseWriteCustomizer<WriteSheet> {

    /**
     * ExcelSheet注解实例，包含自定义的工作表配置信息
     */
    private final ExcelSheet excelSheet;

    /**
     * 私有构造方法，根据ExcelSheet注解创建WriteSheetCustomizer实例
     *
     * @param excelSheet ExcelSheet注解，用于获取工作表配置信息
     */
    private WriteSheetCustomizer(ExcelSheet excelSheet) {
        super(excelSheet.config());
        this.excelSheet = excelSheet;
    }

    /**
     * 创建并返回一个根据excelSheet配置定制化的WriteSheet对象
     *
     * @param excelSheet ExcelSheet注解，用于获取工作表配置信息
     * @return 返回定制化的WriteSheet对象
     */
    public static WriteSheet getWriteSheet(ExcelSheet excelSheet) {
        WriteSheet writeSheet = new WriteSheet();
        WriteSheetCustomizer writeSheetCustomizer = new WriteSheetCustomizer(excelSheet);
        writeSheetCustomizer.customize(writeSheet);
        return writeSheet;
    }

    /**
     * 根据ExcelSheet数组创建并返回一个定制化的WriteSheet对象列表
     *
     * @param excelSheets ExcelSheet注解数组，用于获取多个工作表的配置信息
     * @return 返回定制化的WriteSheet对象列表
     */
    public static List<WriteSheet> getWriteSheets(ExcelSheet[] excelSheets) {
        return getWriteSheets(List.of(excelSheets));
    }

    /**
     * 根据ExcelSheet列表创建并返回一个定制化的WriteSheet对象列表
     *
     * @param excelSheets ExcelSheet注解列表，用于获取多个工作表的配置信息
     * @return 返回定制化的WriteSheet对象列表
     */
    public static List<WriteSheet> getWriteSheets(List<ExcelSheet> excelSheets) {
        return excelSheets.stream().map(WriteSheetCustomizer::getWriteSheet).toList();
    }

    /**
     * 自定义WriteSheet对象的写入属性
     * 根据excelSheet中的配置信息，设置writeSheet对象的属性
     *
     * @param writeSheet 待定制化的WriteSheet对象
     */
    @Override
    protected void customizeWrite(WriteSheet writeSheet) {
        // 设置工作表编号，用于标识工作簿中的工作表位置（从0开始）
        if (ObjUtil.isNotEmpty(excelSheet.sheetNo())) {
            writeSheet.setSheetNo(excelSheet.sheetNo());
        }
        // 设置工作表名称，显示在Excel底部的工作表标签上
        if (ObjUtil.isNotEmpty(excelSheet.sheetName())) {
            writeSheet.setSheetName(excelSheet.sheetName());
        }
    }
}

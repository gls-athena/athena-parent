package com.gls.athena.starter.excel.customizer;

import cn.hutool.core.util.ObjUtil;
import cn.idev.excel.write.metadata.WriteSheet;
import com.gls.athena.starter.excel.annotation.ExcelSheet;

import java.util.List;

/**
 * Excel工作表写入转换器
 * <p>
 * 用于将ExcelSheet注解配置转换为EasyExcel的WriteSheet参数对象。
 * WriteSheet用于配置Excel工作表的基本信息，如工作表编号、名称等，
 * 是Excel写入操作中工作表级别的配置载体。
 * </p>
 *
 * @author athena-starter-excel
 * @since 1.0.0
 */
public class WriteSheetCustomizer extends BaseWriteCustomizer<WriteSheet> {

    private final ExcelSheet excelSheet;

    private WriteSheetCustomizer(ExcelSheet excelSheet) {
        super(excelSheet.config());
        this.excelSheet = excelSheet;
    }

    public static WriteSheet getWriteSheet(ExcelSheet excelSheet) {
        WriteSheet writeSheet = new WriteSheet();
        WriteSheetCustomizer writeSheetCustomizer = new WriteSheetCustomizer(excelSheet);
        writeSheetCustomizer.customize(writeSheet);
        return writeSheet;
    }

    public static List<WriteSheet> getWriteSheets(ExcelSheet[] excelSheets) {
        return getWriteSheets(List.of(excelSheets));
    }

    public static List<WriteSheet> getWriteSheets(List<ExcelSheet> excelSheets) {
        return excelSheets.stream().map(WriteSheetCustomizer::getWriteSheet).toList();
    }

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

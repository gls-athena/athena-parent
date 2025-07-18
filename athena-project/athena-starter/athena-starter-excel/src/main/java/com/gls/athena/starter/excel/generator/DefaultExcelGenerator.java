package com.gls.athena.starter.excel.generator;

import cn.hutool.core.util.StrUtil;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.WriteTable;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.annotation.ExcelSheet;
import com.gls.athena.starter.excel.customizer.WriteSheetCustomizer;
import com.gls.athena.starter.excel.customizer.WriteTableCustomizer;
import com.gls.athena.starter.excel.customizer.WriteWorkbookCustomizer;
import com.gls.athena.starter.excel.support.ExcelDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;

/**
 * 默认的Excel生成器实现类
 * 负责将数据导出为Excel文件
 *
 * @author lizy19
 */
@Slf4j
@Component
public class DefaultExcelGenerator implements ExcelGenerator {
    /**
     * 生成Excel文件的主要方法
     *
     * @param data          导出的数据
     * @param excelResponse Excel响应注解信息
     * @param outputStream  输出流，用于写入Excel文件
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void generate(Object data, ExcelResponse excelResponse, OutputStream outputStream) throws Exception {
        try (ExcelWriter excelWriter = WriteWorkbookCustomizer.getExcelWriter(excelResponse, outputStream)) {
            // 获取并验证工作表配置
            List<ExcelSheet> sheets = ExcelDataUtil.getValidatedSheets(excelResponse);
            // 直接写入数据方式
            writeDataExcel(data, excelWriter, sheets);
        } catch (Exception e) {
            log.error("Excel导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    /**
     * 将数据写入Excel
     *
     * @param data        导出的数据
     * @param excelWriter Excel写入器
     * @param sheets      工作表配置列表
     */
    private void writeDataExcel(Object data, ExcelWriter excelWriter, List<ExcelSheet> sheets) {
        for (ExcelSheet sheet : sheets) {
            // 根据工作表索引获取对应的数据
            Object sheetData = ExcelDataUtil.getDataAtIndex(data, sheets, sheet.sheetNo());
            WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(sheet);
            List<WriteTable> writeTables = WriteTableCustomizer.getWriteTables(sheet.tables());

            if (writeTables.isEmpty()) {
                // 没有配置表格，直接写入工作表
                writeDataToSheet(sheetData, excelWriter, writeSheet, null);
            } else {
                // 有配置表格，按表格分别写入数据
                for (WriteTable writeTable : writeTables) {
                    Object tableData = ExcelDataUtil.getDataAtIndex(sheetData, writeTables, writeTable.getTableNo());
                    writeDataToSheet(tableData, excelWriter, writeSheet, writeTable);
                }
            }
        }
    }

    /**
     * 将数据写入指定的工作表或表格
     *
     * @param data        导出的数据
     * @param excelWriter Excel写入器
     * @param writeSheet  工作表配置
     * @param writeTable  表格配置，如果没有表格配置则为null
     */
    private void writeDataToSheet(Object data, ExcelWriter excelWriter, WriteSheet writeSheet, WriteTable writeTable) {
        // 标准化数据为List格式
        List<?> dataList = ExcelDataUtil.normalizeToList(data);
        if (dataList.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        // 验证数据类型一致性
        Class<?> clazz = dataList.getFirst().getClass();
        for (Object item : dataList) {
            if (item == null || !clazz.equals(item.getClass())) {
                throw new IllegalArgumentException("数据列表元素类型不一致或包含null元素");
            }
        }

        // 根据是否有表格配置选择写入方式
        if (writeTable != null) {
            writeTable.setClazz(clazz);
            excelWriter.write(dataList, writeSheet, writeTable);
        } else {
            writeSheet.setClazz(clazz);
            excelWriter.write(dataList, writeSheet);
        }
    }

    /**
     * 判断当前生成器是否支持指定的Excel响应配置
     *
     * @param excelResponse Excel响应注解信息
     * @return 如果支持返回true，否则返回false
     */
    @Override
    public boolean supports(ExcelResponse excelResponse) {
        return StrUtil.isBlank(excelResponse.template()) && excelResponse.generator() == ExcelGenerator.class;
    }
}

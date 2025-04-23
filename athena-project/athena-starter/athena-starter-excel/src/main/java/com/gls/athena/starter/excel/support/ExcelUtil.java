package com.gls.athena.starter.excel.support;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

/**
 * @author lizy19
 */
@UtilityClass
public class ExcelUtil {
    /**
     * 将数据写入Excel文件中的多个Sheet和Table。
     *
     * @param excelWriter    Excel写入工具，用于将数据写入Excel文件
     * @param writeSheetList 需要写入的Sheet列表，每个Sheet对应一个WriteSheet对象
     * @param writeTableMap  每个Sheet对应的Table列表，键为Sheet编号，值为WriteTable列表
     * @param data           需要写入Excel的数据列表
     */
    public void writeData(ExcelWriter excelWriter, List<WriteSheet> writeSheetList, Map<Integer, List<WriteTable>> writeTableMap, List<?> data) {
        // 遍历所有WriteSheet，逐个处理每个sheet的数据写入
        for (WriteSheet writeSheet : writeSheetList) {
            // 获取当前sheet对应的数据
            List<?> sheetData = getSheetData(writeSheet, data, writeSheetList.size());
            if (sheetData.isEmpty()) {
                continue;
            }

            // 获取当前sheet对应的WriteTable列表
            List<WriteTable> writeTableList = writeTableMap.get(writeSheet.getSheetNo());
            if (CollUtil.isNotEmpty(writeTableList)) {
                // 如果存在WriteTable列表，则将数据写入对应的table中
                writeTableData(excelWriter, writeSheet, writeTableList, sheetData);
            } else {
                // 如果不存在WriteTable列表，则将数据直接写入sheet中
                writeSheetData(excelWriter, writeSheet, sheetData);
            }
        }
    }

    /**
     * 获取sheet数据
     *
     * @param writeSheet 包含sheet信息的对象，用于获取sheet的序号
     * @param data       包含所有sheet数据的列表，每个元素可能是一个列表，表示单个sheet的数据
     * @param sheetCount 指定返回数据的sheet数量。如果为1，则返回全部数据；否则返回对应sheet序号的数据
     * @return 返回指定sheet的数据列表。如果sheetCount为1，则返回整个数据列表；否则返回对应sheet序号的数据列表
     */
    public List<?> getSheetData(WriteSheet writeSheet, List<?> data, int sheetCount) {
        if (data == null) {
            throw new IllegalArgumentException("数据列表不能为null");
        }

        if (sheetCount == 1) {
            return data;
        }

        int sheetNo = writeSheet.getSheetNo();
        if (sheetNo < 0 || sheetNo >= data.size()) {
            throw new IllegalArgumentException("sheet序号超出范围");
        }

        Object sheetData = data.get(sheetNo);
        if (!(sheetData instanceof List)) {
            throw new IllegalArgumentException("指定sheet的数据不是列表类型");
        }

        return (List<?>) sheetData;
    }

    /**
     * 将表格数据写入Excel文件。
     *
     * @param excelWriter    Excel写入工具，用于执行实际的写入操作。
     * @param writeSheet     写入的工作表对象，指定数据写入的目标工作表。
     * @param writeTableList 包含所有需要写入的表格信息的列表，每个表格信息包括表格编号、样式等。
     * @param sheetData      包含所有表格数据的列表，每个表格数据对应一个子列表。
     */
    public void writeTableData(ExcelWriter excelWriter, WriteSheet writeSheet, List<WriteTable> writeTableList, List<?> sheetData) {
        // 遍历所有需要写入的表格信息
        for (WriteTable writeTable : writeTableList) {
            // 获取当前表格的编号，并检查其有效性
            int tableNo = writeTable.getTableNo();
            if (tableNo < 0 || tableNo >= sheetData.size()) {
                continue; // 如果表格编号无效，则跳过该表格
            }

            // 获取当前表格对应的数据
            List<?> tableData = (List<?>) sheetData.get(tableNo);

            // 如果表格数据不为空，则设置表格的类类型并执行写入操作
            if (!tableData.isEmpty() && tableData.getFirst() != null) {
                writeTable.setClazz(tableData.getFirst().getClass());
                excelWriter.write(tableData, writeSheet, writeTable);
            }
        }
    }

    /**
     * 写入sheet数据
     * <p>
     * 该方法用于将数据写入Excel的单个sheet中。首先设置sheet的数据类型，然后将数据写入指定的sheet。
     *
     * @param excelWriter Excel写入器，用于执行实际的写入操作。
     * @param writeSheet  要写入的sheet对象，包含sheet的配置信息。
     * @param sheetData   要写入的数据列表，列表中的元素类型应与sheet的数据类型一致。
     */
    public void writeSheetData(ExcelWriter excelWriter, WriteSheet writeSheet, List<?> sheetData) {
        // 检查sheetData是否为空
        if (sheetData == null || sheetData.isEmpty()) {
            throw new IllegalArgumentException("sheetData不能为空");
        }

        // 获取第一个元素的类型
        Class<?> clazz = sheetData.getFirst().getClass();

        // 检查sheetData中所有元素的类型是否一致
        for (Object data : sheetData) {
            if (data == null || !clazz.equals(data.getClass())) {
                throw new IllegalArgumentException("sheetData中的元素类型不一致");
            }
        }

        // 设置sheet的数据类型
        writeSheet.setClazz(clazz);

        // 将数据写入指定的sheet
        excelWriter.write(sheetData, writeSheet);
    }

}

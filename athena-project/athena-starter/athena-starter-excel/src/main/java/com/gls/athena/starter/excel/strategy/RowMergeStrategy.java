package com.gls.athena.starter.excel.strategy;

import cn.hutool.poi.excel.cell.CellUtil;
import cn.idev.excel.metadata.Head;
import cn.idev.excel.write.merge.AbstractMergeStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

/**
 * 行合并策略 <br> 横向合并相同行的单元格
 *
 * @author george
 */
@RequiredArgsConstructor
public class RowMergeStrategy extends AbstractMergeStrategy {

    /**
     * 合并列索引
     */
    private final Integer columnIndex;
    /**
     * 合并行的值
     */
    private final List<Object> rowValues;

    /**
     * 合并单元格
     * <p>
     * 该函数用于根据当前单元格的值和位置，判断是否需要合并单元格。如果满足条件，则对指定范围内的单元格进行合并操作。
     *
     * @param sheet            当前工作表对象，表示要操作的Excel表格页
     * @param cell             当前单元格对象，表示要处理的单元格
     * @param head             表头对象，包含表头相关信息
     * @param relativeRowIndex 相对行索引，表示当前行相对于某个基准行的偏移量
     */
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        // 获取当前单元格的行索引和列索引
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();

        // 获取当前单元格的值
        Object cellValue = CellUtil.getCellValue(cell);

        // 判断是否需要合并单元格：如果当前列索引小于指定列索引，并且当前单元格的值在预定义的行值列表中
        if (columnIndex < this.columnIndex && rowValues.contains(cellValue)) {
            // 如果当前单元格未被合并，则进行合并操作
            if (!CellUtil.isMergedRegion(cell)) {
                CellUtil.mergingCells(sheet, rowIndex, rowIndex, columnIndex, this.columnIndex);
            }
        }
    }

}

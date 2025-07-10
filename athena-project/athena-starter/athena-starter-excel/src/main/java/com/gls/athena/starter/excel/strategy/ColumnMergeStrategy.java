package com.gls.athena.starter.excel.strategy;

import cn.hutool.poi.excel.cell.CellUtil;
import cn.idev.excel.metadata.Head;
import cn.idev.excel.write.merge.AbstractMergeStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * 列合并策略 <br> 纵向合并相同列的单元格 <br> 例如：合并第1列和第2列相同的单元格
 *
 * @author george
 */
@RequiredArgsConstructor
public class ColumnMergeStrategy extends AbstractMergeStrategy {
    /**
     * 忽略的值
     */
    private final List<Object> ignoreValues;
    /**
     * 合并列索引
     */
    private final List<Integer> columnIndexes;

    /**
     * 合并单元格
     * <p>
     * 该方法用于根据当前单元格的值和列索引，决定是否合并单元格。如果当前单元格的值在忽略的值列表中，则不进行合并操作。
     * 如果当前单元格的列索引在指定的列索引列表中，则调用合并单元格的方法进行合并。
     *
     * @param sheet            当前表格对象，表示要操作的Excel表格
     * @param cell             当前单元格对象，表示要处理的单元格
     * @param head             当前单元格的表头对象，表示单元格的表头信息
     * @param relativeRowIndex 相对行索引，表示当前单元格相对于某个基准行的偏移量
     */
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        // 获取当前单元格的行索引和列索引
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();

        // 如果当前单元格的值在忽略的值列表中，则不进行合并操作
        if (ignoreValues.contains(CellUtil.getCellValue(cell))) {
            return;
        }

        // 如果当前单元格的列索引在指定的列索引列表中，则调用合并单元格的方法进行合并
        if (columnIndexes.contains(columnIndex)) {
            mergeCell(sheet, cell, rowIndex, columnIndex);
        }
    }

    /**
     * 合并单元格
     * <p>
     * 该方法用于检查当前单元格的值是否与上一行相同列的值相同，如果相同则合并这两个单元格。
     * 合并操作会移除原有的合并区域，并重新创建一个新的合并区域。
     *
     * @param sheet       当前操作的表格对象，表示要合并单元格的工作表
     * @param cell        当前单元格对象，表示要检查的单元格
     * @param rowIndex    当前单元格所在的行索引
     * @param columnIndex 当前单元格所在的列索引
     */
    private void mergeCell(Sheet sheet, Cell cell, int rowIndex, int columnIndex) {
        // 获取上一行相同列的单元格
        Cell preCell = sheet.getRow(rowIndex - 1).getCell(columnIndex);

        // 获取上一行单元格的值
        Object preCellValue = CellUtil.getCellValue(preCell);

        // 获取当前单元格的值
        Object cellValue = CellUtil.getCellValue(cell);

        // 如果当前单元格的值与上一行相同列的值相同，则进行合并操作
        if (preCellValue.equals(cellValue)) {
            // 获取上一个单元格所在的合并区域的首个单元格
            Cell firstCell = CellUtil.getMergedRegionCell(preCell);

            // 移除原有的合并区域
            removeMergedRegion(sheet, firstCell);

            // 重新合并单元格，范围从上一行的单元格到当前单元格
            CellUtil.mergingCells(sheet, firstCell.getRowIndex(), rowIndex, columnIndex, columnIndex);
        }
    }

    /**
     * 移除包含指定单元格的合并区域。
     * <p>
     * 该方法会遍历当前表格中的所有合并区域，检查指定的单元格是否位于某个合并区域内。
     * 如果找到包含该单元格的合并区域，则将该合并区域从表格中移除。
     *
     * @param sheet 当前表格对象，表示要操作的表格。
     * @param cell  指定的单元格对象，表示要检查的单元格。
     */
    private void removeMergedRegion(Sheet sheet, Cell cell) {
        // 获取当前表格中的所有合并区域
        List<CellRangeAddress> list = sheet.getMergedRegions();

        // 遍历所有合并区域，检查是否包含指定的单元格
        for (int i = 0; i < list.size(); i++) {
            CellRangeAddress cellRangeAddress = list.get(i);
            if (cellRangeAddress.isInRange(cell)) {
                // 如果找到包含指定单元格的合并区域，则移除该合并区域
                sheet.removeMergedRegion(i);
            }
        }
    }

}

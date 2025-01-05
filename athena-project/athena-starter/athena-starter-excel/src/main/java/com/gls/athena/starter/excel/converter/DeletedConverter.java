package com.gls.athena.starter.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * Excel 删除标记转换器
 * 用于在 Excel 导入导出时转换删除状态
 * - Excel中的"已删除"对应Java中的true
 * - Excel中的"正常"对应Java中的false
 *
 * @author george
 */
public class DeletedConverter implements Converter<Boolean> {

    /**
     * Excel中表示删除状态的文本
     */
    private static final String DELETED_TEXT = "已删除";

    /**
     * Excel中表示正常状态的文本
     */
    private static final String NORMAL_TEXT = "正常";

    /**
     * 获取Java数据类型
     *
     * @return 返回Boolean.class，表示转换器支持布尔类型
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        return Boolean.class;
    }

    /**
     * 获取Excel单元格数据类型
     *
     * @return 返回STRING类型，表示Excel中以字符串形式存储
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 将Excel数据转换为Java数据
     *
     * @param cellData            Excel单元格数据
     * @param contentProperty     单元格配置属性
     * @param globalConfiguration 全局配置信息
     * @return true表示已删除，false表示未删除
     */
    @Override
    public Boolean convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (cellData == null || cellData.getStringValue() == null) {
            return Boolean.FALSE;
        }
        return DELETED_TEXT.equals(cellData.getStringValue().trim());
    }

    /**
     * 将Java数据转换为Excel数据
     *
     * @param value               布尔值，true表示已删除，false表示正常
     * @param contentProperty     单元格配置属性
     * @param globalConfiguration 全局配置信息
     * @return 转换后的Excel单元格数据：已删除/正常
     */
    @Override
    public WriteCellData<?> convertToExcelData(Boolean value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String text = Boolean.TRUE.equals(value) ? DELETED_TEXT : NORMAL_TEXT;
        return new WriteCellData<>(text);
    }
}

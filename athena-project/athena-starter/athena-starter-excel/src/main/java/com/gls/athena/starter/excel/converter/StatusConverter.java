package com.gls.athena.starter.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * 状态转换器
 *
 * @author george
 */
public class StatusConverter implements Converter<Integer> {

    /**
     * 支持的Java类型
     *
     * @return Java类型
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    /**
     * 支持的Excel类型
     *
     * @return Excel类型
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 转换Excel数据到Java数据
     *
     * @param cellData            Excel数据
     * @param contentProperty     Excel属性
     * @param globalConfiguration 全局配置
     * @return Java数据
     */
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return "启用".equals(cellData.getStringValue()) ? 1 : 0;
    }

    /**
     * 转换Java数据到Excel数据
     *
     * @param value               Java数据
     * @param contentProperty     Excel属性
     * @param globalConfiguration 全局配置
     * @return Excel数据
     */
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(value == 1 ? "启用" : "禁用");
    }

}

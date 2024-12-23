package com.gls.athena.cloud.sdk.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * 数据源类型转换器
 *
 * @author george
 */
public class DatasourceTypeConverter implements Converter<Integer> {
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
        return switch (cellData.getStringValue()) {
            case "mysql" -> 1;
            case "oracle" -> 2;
            case "sqlserver" -> 3;
            case "postgresql" -> 4;
            default -> 0;
        };
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
        return switch (value) {
            case 1 -> new WriteCellData<>("mysql");
            case 2 -> new WriteCellData<>("oracle");
            case 3 -> new WriteCellData<>("sqlserver");
            case 4 -> new WriteCellData<>("postgresql");
            default -> new WriteCellData<>("");
        };
    }

}

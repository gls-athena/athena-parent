package com.gls.athena.starter.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * 数据源类型转换器
 * 用于Excel导入导出时在String和Integer类型之间进行转换
 *
 * @author george
 */
public class DatasourceTypeConverter implements Converter<Integer> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * Excel数据转Java数据
     * mysql -> 1, oracle -> 2, sqlserver -> 3, postgresql -> 4
     */
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                     GlobalConfiguration globalConfiguration) {
        return switch (cellData.getStringValue().toLowerCase()) {
            case "mysql" -> 1;
            case "oracle" -> 2;
            case "sqlserver" -> 3;
            case "postgresql" -> 4;
            default -> 0;
        };
    }

    /**
     * Java数据转Excel数据
     * 1 -> mysql, 2 -> oracle, 3 -> sqlserver, 4 -> postgresql
     */
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(switch (value) {
            case 1 -> "mysql";
            case 2 -> "oracle";
            case 3 -> "sqlserver";
            case 4 -> "postgresql";
            default -> "";
        });
    }
}

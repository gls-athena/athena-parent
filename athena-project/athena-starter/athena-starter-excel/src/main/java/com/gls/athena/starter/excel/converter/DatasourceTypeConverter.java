package com.gls.athena.starter.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.gls.athena.starter.excel.enums.DatasourceTypeEnums;

import java.util.Optional;

/**
 * 数据源类型转换器
 * 用于Excel导入导出时在字符串类型的数据源名称和整型编码之间进行转换
 * 支持的转换：
 * - mysql(1)
 * - oracle(2)
 * - sqlserver(3)
 * - postgresql(4)
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
     * 将数据源名称字符串转换为对应的编码
     *
     * @param cellData            Excel单元格数据
     * @param contentProperty     Excel内容属性
     * @param globalConfiguration 全局配置
     * @return 数据源类型编码，如果转换失败则返回0
     */
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                     GlobalConfiguration globalConfiguration) {
        return Optional.ofNullable(cellData.getStringValue())
                .map(DatasourceTypeEnums::getByName)
                .map(DatasourceTypeEnums::getCode)
                .orElse(0);

    }

    /**
     * Java数据转Excel数据
     * 将数据源类型编码转换为对应的名称
     *
     * @param value               数据源类型编码
     * @param contentProperty     Excel内容属性
     * @param globalConfiguration 全局配置
     * @return 包含数据源类型名称的单元格数据，如果转换失败则返回空字符串
     */
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(Optional.ofNullable(value)
                .map(DatasourceTypeEnums::getByCode)
                .map(DatasourceTypeEnums::getName)
                .orElse(""));
    }
}

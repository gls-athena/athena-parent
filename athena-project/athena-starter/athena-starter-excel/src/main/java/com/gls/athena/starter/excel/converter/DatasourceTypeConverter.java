package com.gls.athena.starter.excel.converter;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
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
     * 将数据源名称字符串转换为对应的编码。
     * 该方法通过读取Excel单元格中的字符串值，查找对应的数据源类型枚举，并返回其编码。
     * 如果转换失败（例如，字符串值无法匹配任何枚举），则返回默认值0。
     *
     * @param cellData            Excel单元格数据，包含需要转换的字符串值
     * @param contentProperty     Excel内容属性，用于提供额外的上下文信息（未在此方法中使用）
     * @param globalConfiguration 全局配置，用于提供全局的配置信息（未在此方法中使用）
     * @return 数据源类型编码，如果转换失败则返回0
     */
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                     GlobalConfiguration globalConfiguration) {
        // 通过Optional处理可能为空的单元格字符串值，避免空指针异常
        // 1. 获取单元格的字符串值
        // 2. 使用DatasourceTypeEnums的getByName方法查找对应的枚举
        // 3. 如果找到枚举，则获取其编码
        // 4. 如果任何步骤失败，返回默认值0
        return Optional.ofNullable(cellData.getStringValue())
                .map(DatasourceTypeEnums::getByName)
                .map(DatasourceTypeEnums::getCode)
                .orElse(0);
    }

    /**
     * 将Java数据转换为Excel数据。
     * 该方法通过数据源类型编码查找对应的数据源类型名称，并将其封装为Excel单元格数据。
     * 如果编码为空或未找到对应的名称，则返回包含空字符串的单元格数据。
     *
     * @param value               数据源类型编码，可能为null
     * @param contentProperty     Excel内容属性，用于描述Excel单元格的格式和样式
     * @param globalConfiguration 全局配置，包含转换过程中可能需要的全局设置
     * @return 包含数据源类型名称的WriteCellData对象，如果转换失败则返回包含空字符串的WriteCellData对象
     */
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        // 使用Optional处理可能为null的value，通过DatasourceTypeEnums枚举类查找对应的名称
        return new WriteCellData<>(Optional.ofNullable(value)
                .map(DatasourceTypeEnums::getByCode)
                .map(DatasourceTypeEnums::getName)
                .orElse(""));
    }

}

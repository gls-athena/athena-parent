package com.gls.athena.starter.excel.customizer;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.builder.AbstractExcelWriterParameterBuilder;
import com.gls.athena.common.core.base.ICustomizer;
import com.gls.athena.starter.excel.annotation.ExcelParameter;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Locale;

/**
 * Excel写入参数构建器自定义器
 * 用于定制化配置Excel写入参数，支持表头设置、转换器注册、样式处理等功能
 *
 * @param <B> Excel写入参数构建器类型
 * @author george
 */
@Data
public class ExcelWriterParameterBuilderCustomizer<B extends AbstractExcelWriterParameterBuilder<B, ?>> implements ICustomizer<B> {

    /**
     * Excel参数配置
     */
    private final ExcelParameter excelParameter;

    /**
     * 定制Excel写入参数
     *
     * @param builder Excel写入参数构建器
     */
    @Override
    public void customize(B builder) {
        // 配置表头信息
        configureHeader(builder);

        // 配置转换器
        registerConverters(builder);

        // 配置基础参数
        configureBasicParameters(builder);

        // 配置数据处理
        configureDataHandling(builder);

        // 配置列过滤
        configureColumnFilters(builder);
    }

    /**
     * 配置表头信息
     */
    private void configureHeader(B builder) {
        if (ObjUtil.isNotEmpty(excelParameter.head())) {
            builder.head(Arrays.stream(excelParameter.head())
                    .map(head -> StrUtil.split(head, StrUtil.COMMA))
                    .toList());
        }
        builder.needHead(excelParameter.needHead());
        builder.automaticMergeHead(excelParameter.automaticMergeHead());
        if (excelParameter.relativeHeadRowIndex() > 0) {
            builder.relativeHeadRowIndex(excelParameter.relativeHeadRowIndex());
        }
    }

    /**
     * 注册数据转换器
     */
    private void registerConverters(B builder) {
        if (excelParameter.converter() != null) {
            Arrays.stream(excelParameter.converter())
                    .forEach(converter -> builder.registerConverter(BeanUtils.instantiateClass(converter)));
        }
    }

    /**
     * 配置基础参数
     */
    private void configureBasicParameters(B builder) {
        builder.use1904windowing(excelParameter.use1904windowing());
        if (StrUtil.isNotEmpty(excelParameter.locale())) {
            builder.locale(Locale.of(excelParameter.locale()));
        }
        if (excelParameter.filedCacheLocation() != null) {
            builder.filedCacheLocation(excelParameter.filedCacheLocation());
        }
        builder.autoTrim(excelParameter.autoTrim());
        builder.useDefaultStyle(excelParameter.useDefaultStyle());
    }

    /**
     * 配置数据处理
     */
    private void configureDataHandling(B builder) {
        if (excelParameter.writeHandler() != null) {
            Arrays.stream(excelParameter.writeHandler())
                    .forEach(handler -> builder.registerWriteHandler(BeanUtils.instantiateClass(handler)));
        }
    }

    /**
     * 配置列过滤
     */
    private void configureColumnFilters(B builder) {
        // 设置排除的列
        if (ArrayUtil.isNotEmpty(excelParameter.excludeColumnIndexes())) {
            builder.excludeColumnIndexes(Arrays.stream(excelParameter.excludeColumnIndexes()).boxed().toList());
        }
        if (ArrayUtil.isNotEmpty(excelParameter.excludeColumnFieldNames())) {
            builder.excludeColumnFieldNames(Arrays.stream(excelParameter.excludeColumnFieldNames()).toList());
        }

        // 设置包含的列
        if (ArrayUtil.isNotEmpty(excelParameter.includeColumnIndexes())) {
            builder.includeColumnIndexes(Arrays.stream(excelParameter.includeColumnIndexes()).boxed().toList());
        }
        if (ArrayUtil.isNotEmpty(excelParameter.includeColumnFieldNames())) {
            builder.includeColumnFieldNames(Arrays.stream(excelParameter.includeColumnFieldNames()).toList());
        }

        builder.orderByIncludeColumn(excelParameter.orderByIncludeColumn());
    }
}

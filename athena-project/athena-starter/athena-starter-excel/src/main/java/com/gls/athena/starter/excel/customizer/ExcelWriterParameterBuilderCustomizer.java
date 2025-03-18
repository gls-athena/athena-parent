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
     * <p>
     * 该函数用于定制Excel写入时的各项参数，包括表头信息、转换器、基础参数、数据处理和列过滤等。
     * 通过调用不同的配置方法，逐步完成对Excel写入参数的定制化设置。
     *
     * @param builder Excel写入参数构建器，用于构建和配置Excel写入参数
     */
    @Override
    public void customize(B builder) {
        // 配置表头信息：设置Excel文件的表头样式、内容等
        configureHeader(builder);

        // 配置转换器：注册数据转换器，用于处理特定类型的数据转换
        registerConverters(builder);

        // 配置基础参数：设置Excel写入的基本参数，如文件格式、编码等
        configureBasicParameters(builder);

        // 配置数据处理：设置数据处理逻辑，如数据校验、格式化等
        configureDataHandling(builder);

        // 配置列过滤：设置列过滤规则，决定哪些列需要写入Excel
        configureColumnFilters(builder);
    }

    /**
     * 配置表头信息
     * <p>
     * 该函数根据传入的 `excelParameter` 配置表头相关信息，并将配置应用到 `builder` 对象中。
     *
     * @param builder 用于构建表头的构建器对象，类型为泛型 `B`，具体类型由调用方决定。
     *                <p>
     *                函数执行逻辑：
     *                1. 如果 `excelParameter.head()` 不为空，则将表头字符串按逗号分隔，并将结果列表设置到 `builder` 中。
     *                2. 设置 `builder` 是否需要表头 (`needHead`) 以及是否自动合并表头 (`automaticMergeHead`)。
     *                3. 如果 `excelParameter.relativeHeadRowIndex()` 大于 0，则设置 `builder` 的相对表头行索引。
     */
    private void configureHeader(B builder) {
        // 配置表头内容
        if (ObjUtil.isNotEmpty(excelParameter.head())) {
            builder.head(Arrays.stream(excelParameter.head())
                    .map(head -> StrUtil.split(head, StrUtil.COMMA))
                    .toList());
        }

        // 配置是否需要表头及是否自动合并表头
        builder.needHead(excelParameter.needHead());
        builder.automaticMergeHead(excelParameter.automaticMergeHead());

        // 配置相对表头行索引
        if (excelParameter.relativeHeadRowIndex() > 0) {
            builder.relativeHeadRowIndex(excelParameter.relativeHeadRowIndex());
        }
    }

    /**
     * 注册数据转换器
     * <p>
     * 该方法用于根据`excelParameter`中配置的转换器类，动态实例化并注册到`builder`中。
     *
     * @param builder 用于注册转换器的构建器对象，类型为泛型`B`，通常是一个支持注册转换器的构建器实例。
     */
    private void registerConverters(B builder) {
        // 检查`excelParameter`中是否配置了转换器
        if (excelParameter.converter() != null) {
            // 遍历所有配置的转换器类，实例化并注册到`builder`中
            Arrays.stream(excelParameter.converter())
                    .forEach(converter -> builder.registerConverter(BeanUtils.instantiateClass(converter)));
        }
    }

    /**
     * 配置基础参数
     * <p>
     * 该方法用于根据传入的 `excelParameter` 对象配置 `builder` 的基础参数。
     * 配置包括是否使用1904日期系统、区域设置、文件缓存位置、自动修剪和默认样式等。
     *
     * @param builder 需要配置的构建器对象，类型为泛型 `B`，通常是一个用于构建 Excel 相关配置的构建器。
     */
    private void configureBasicParameters(B builder) {
        // 配置是否使用1904日期系统
        builder.use1904windowing(excelParameter.use1904windowing());

        // 如果区域设置不为空，则配置区域设置
        if (StrUtil.isNotEmpty(excelParameter.locale())) {
            builder.locale(Locale.of(excelParameter.locale()));
        }

        // 如果文件缓存位置不为空，则配置文件缓存位置
        if (excelParameter.filedCacheLocation() != null) {
            builder.filedCacheLocation(excelParameter.filedCacheLocation());
        }

        // 配置是否自动修剪
        builder.autoTrim(excelParameter.autoTrim());

        // 配置是否使用默认样式
        builder.useDefaultStyle(excelParameter.useDefaultStyle());
    }

    /**
     * 配置数据处理
     * <p>
     * 该函数用于根据 `excelParameter` 中的配置，注册写处理器到 `builder` 中。
     * 如果 `excelParameter.writeHandler()` 返回的处理器数组不为空，则遍历该数组，
     * 并将每个处理器实例化后注册到 `builder` 中。
     *
     * @param builder 用于注册写处理器的构建器对象，类型为泛型 `B`
     */
    private void configureDataHandling(B builder) {
        // 检查是否存在写处理器配置
        if (excelParameter.writeHandler() != null) {
            // 遍历所有写处理器，并注册到 builder 中
            Arrays.stream(excelParameter.writeHandler())
                    .forEach(handler -> builder.registerWriteHandler(BeanUtils.instantiateClass(handler)));
        }
    }

    /**
     * 配置列过滤规则，根据Excel参数设置排除或包含的列，并指定是否按包含列排序。
     *
     * @param builder 用于构建列过滤规则的构建器对象，类型为泛型B。
     *                通过该对象设置排除列、包含列以及排序规则。
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

        // 设置是否按包含列排序
        builder.orderByIncludeColumn(excelParameter.orderByIncludeColumn());
    }

}

package com.gls.athena.starter.excel.customizer;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.write.builder.AbstractExcelWriterParameterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.gls.athena.common.core.base.ICustomizer;
import com.gls.athena.starter.excel.annotation.ExcelParameter;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Locale;

/**
 * Excel写入构建器自定义器
 *
 * @param <B> AbstractExcelWriterParameterBuilder
 * @author george
 */
@Data
public class ExcelWriterParameterBuilderCustomizer<B extends AbstractExcelWriterParameterBuilder<B, ?>> implements ICustomizer<B> {

    /**
     * Excel参数
     */
    private final ExcelParameter excelParameter;

    /**
     * 定制
     *
     * @param builder 定制对象
     */
    @Override
    public void customize(B builder) {
        // 表头
        if (ObjUtil.isNotEmpty(excelParameter.head())) {
            builder.head(Arrays.stream(excelParameter.head())
                    .map(head -> StrUtil.split(head, StrUtil.COMMA)).toList());
        }
        // 注册转换器
        if (excelParameter.converter() != null) {
            for (Class<? extends Converter<?>> converter : excelParameter.converter()) {
                builder.registerConverter(BeanUtils.instantiateClass(converter));
            }
        }
        // excel中时间是存储1900年起的一个双精度浮点数，但是有时候默认开始日期是1904，所以设置这个值改成默认1904年开始
        builder.use1904windowing(excelParameter.use1904windowing());
        // 区域
        if (StrUtil.isNotEmpty(excelParameter.locale())) {
            builder.locale(Locale.of(excelParameter.locale()));
        }
        // 字段缓存位置
        if (excelParameter.filedCacheLocation() != null) {
            builder.filedCacheLocation(excelParameter.filedCacheLocation());
        }
        // 自动去除空格 会对头、读取数据等进行自动trim
        builder.autoTrim(excelParameter.autoTrim());
        // 写入到excel和上面空开几行
        if (excelParameter.relativeHeadRowIndex() > 0) {
            builder.relativeHeadRowIndex(excelParameter.relativeHeadRowIndex());
        }
        // 是否需要写入头到excel
        builder.needHead(excelParameter.needHead());
        // 注册写处理器
        if (excelParameter.writeHandler() != null) {
            for (Class<? extends WriteHandler> writeHandler : excelParameter.writeHandler()) {
                builder.registerWriteHandler(BeanUtils.instantiateClass(writeHandler));
            }
        }
        // 是否使用默认的样式
        builder.useDefaultStyle(excelParameter.useDefaultStyle());
        // 自动合并头，头中相同的字段上下左右都会去尝试匹配
        builder.automaticMergeHead(excelParameter.automaticMergeHead());
        // 需要排除对象中的index的数据
        if (ArrayUtil.isNotEmpty(excelParameter.excludeColumnIndexes())) {
            builder.excludeColumnIndexes(Arrays.stream(excelParameter.excludeColumnIndexes()).boxed().toList());
        }
        // 需要排除对象中的字段的数据
        if (ArrayUtil.isNotEmpty(excelParameter.excludeColumnFieldNames())) {
            builder.excludeColumnFieldNames(Arrays.stream(excelParameter.excludeColumnFieldNames()).toList());
        }
        // 只要导出对象中的index的数据
        if (ArrayUtil.isNotEmpty(excelParameter.includeColumnIndexes())) {
            builder.includeColumnIndexes(Arrays.stream(excelParameter.includeColumnIndexes()).boxed().toList());
        }
        // 只要导出对象中的字段的数据
        if (ArrayUtil.isNotEmpty(excelParameter.includeColumnFieldNames())) {
            builder.includeColumnFieldNames(Arrays.stream(excelParameter.includeColumnFieldNames()).toList());
        }
        // 在使用了参数includeColumnFieldNames 或者 includeColumnIndexes的时候，会根据传入集合的顺序排序
        builder.orderByIncludeColumn(excelParameter.orderByIncludeColumn());
    }

}

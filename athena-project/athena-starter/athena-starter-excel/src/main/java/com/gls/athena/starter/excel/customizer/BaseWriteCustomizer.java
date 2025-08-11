package com.gls.athena.starter.excel.customizer;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.write.handler.WriteHandler;
import cn.idev.excel.write.metadata.WriteBasicParameter;
import com.gls.athena.common.core.base.ICustomizer;
import com.gls.athena.starter.excel.annotation.ExcelConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * 抽象的Excel写入自定义类，提供了一些默认的自定义行为
 *
 * @param <Parameter> 扩展WriteBasicParameter的参数类型
 * @author george
 */
@RequiredArgsConstructor
public abstract class BaseWriteCustomizer<Parameter extends WriteBasicParameter> implements ICustomizer<Parameter> {

    private final ExcelConfig config;

    /**
     * 执行自定义行为，包括基础设置、写入基础设置和特定于写的设置
     *
     * @param parameter 扩展WriteBasicParameter的参数实例
     */
    @Override
    public void customize(Parameter parameter) {
        // 执行基础自定义设置
        customizeBasic(parameter);
        // 执行写入基础自定义设置
        customizeWriteBasic(parameter);
        // 执行特定于写的自定义设置
        customizeWrite(parameter);
    }

    /**
     * 子类需要实现的方法，用于执行特定于写的自定义行为
     *
     * @param parameter 扩展WriteBasicParameter的参数实例
     */
    protected abstract void customizeWrite(Parameter parameter);

    /**
     * 设置一些基本的参数，如表头、实体类、转换器等
     *
     * @param parameter 扩展WriteBasicParameter的参数实例
     */
    private void customizeBasic(Parameter parameter) {
        // 设置表头信息，支持多级表头（用逗号分隔）
        if (ObjUtil.isNotEmpty(config.head())) {
            List<List<String>> head = Arrays.stream(config.head())
                    .map(s -> StrUtil.split(s, StrUtil.COMMA))
                    .toList();
            parameter.setHead(head);
        }

        // 设置数据对应的实体类
        if (ObjUtil.isNotNull(config.clazz())) {
            parameter.setClazz(config.clazz());
        }

        // 设置自定义转换器列表
        if (ObjUtil.isNotEmpty(config.converter())) {
            List<Converter<?>> customConverterList = Optional.ofNullable(parameter.getCustomConverterList())
                    .orElse(new ArrayList<>());

            Arrays.stream(config.converter())
                    .map(BeanUtils::instantiateClass)
                    .forEach(customConverterList::add);

            parameter.setCustomConverterList(customConverterList);
        }

        // 设置是否自动去除字符串两端空格
        if (ObjUtil.isNotNull(config.autoTrim())) {
            parameter.setAutoTrim(config.autoTrim());
        }

        // 设置是否使用1904窗口系统（主要用于Mac Excel兼容性）
        if (ObjUtil.isNotNull(config.use1904windowing())) {
            parameter.setUse1904windowing(config.use1904windowing());
        }

        // 设置本地化信息
        if (StrUtil.isNotBlank(config.locale())) {
            Locale locale = Locale.forLanguageTag(config.locale());
            parameter.setLocale(locale);
        }

        // 设置字段缓存位置
        if (ObjUtil.isNotNull(config.filedCacheLocation())) {
            parameter.setFiledCacheLocation(config.filedCacheLocation());
        }
    }

    /**
     * 设置一些写入相关的基础参数，如表头行索引、是否需要表头、写入处理器等
     *
     * @param parameter 扩展WriteBasicParameter的参数实例
     */
    private void customizeWriteBasic(Parameter parameter) {
        // 设置相对表头行索引
        if (ObjUtil.isNotNull(config.relativeHeadRowIndex())) {
            parameter.setRelativeHeadRowIndex(config.relativeHeadRowIndex());
        }

        // 设置是否需要表头
        if (ObjUtil.isNotNull(config.needHead())) {
            parameter.setNeedHead(config.needHead());
        }

        // 设置自定义写入处理器列表
        if (ObjUtil.isNotEmpty(config.writeHandler())) {
            List<WriteHandler> customWriteHandlerList = Optional.ofNullable(parameter.getCustomWriteHandlerList())
                    .orElse(new ArrayList<>());

            Arrays.stream(config.writeHandler())
                    .map(BeanUtils::instantiateClass)
                    .forEach(customWriteHandlerList::add);

            parameter.setCustomWriteHandlerList(customWriteHandlerList);
        }

        // 设置是否使用默认样式
        if (ObjUtil.isNotNull(config.useDefaultStyle())) {
            parameter.setUseDefaultStyle(config.useDefaultStyle());
        }

        // 设置是否自动合并表头
        if (ObjUtil.isNotNull(config.automaticMergeHead())) {
            parameter.setAutomaticMergeHead(config.automaticMergeHead());
        }

        // 设置排除的列索引列表
        if (ObjUtil.isNotEmpty(config.excludeColumnIndexes())) {
            List<Integer> excludeColumnIndexes = Arrays.stream(config.excludeColumnIndexes())
                    .boxed()
                    .toList();
            parameter.setExcludeColumnIndexes(excludeColumnIndexes);
        }

        // 设置排除的列字段名列表
        if (ObjUtil.isNotEmpty(config.excludeColumnFieldNames())) {
            List<String> excludeColumnFieldNames = Arrays.stream(config.excludeColumnFieldNames())
                    .toList();
            parameter.setExcludeColumnFieldNames(excludeColumnFieldNames);
        }

        // 设置包含的列索引列表
        if (ObjUtil.isNotEmpty(config.includeColumnIndexes())) {
            List<Integer> includeColumnIndexes = Arrays.stream(config.includeColumnIndexes())
                    .boxed()
                    .toList();
            parameter.setIncludeColumnIndexes(includeColumnIndexes);
        }

        // 设置包含的列字段名列表
        if (ObjUtil.isNotEmpty(config.includeColumnFieldNames())) {
            List<String> includeColumnFieldNames = Arrays.stream(config.includeColumnFieldNames())
                    .toList();
            parameter.setIncludeColumnFieldNames(includeColumnFieldNames);
        }

        // 设置是否按包含列排序
        if (ObjUtil.isNotNull(config.orderByIncludeColumn())) {
            parameter.setOrderByIncludeColumn(config.orderByIncludeColumn());
        }
    }

}

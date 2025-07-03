package com.gls.athena.starter.word.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * HTML标签转换器管理器
 * 负责管理和提供HTML标签转换器
 *
 * @author lizy19
 */
@Slf4j
@Component
public class HtmlTagConverterManager {

    private final List<HtmlTagConverter> converters;

    @Autowired
    public HtmlTagConverterManager(List<HtmlTagConverter> converters) {
        this.converters = converters;
        log.info("已加载 {} 个HTML标签转换器", converters.size());
    }

    /**
     * 根据标签名获取对应的转换器
     *
     * @param tagName HTML标签名（小写）
     * @return 对应的转换器，如果没有找到则返回空
     */
    public Optional<HtmlTagConverter> getConverter(String tagName) {
        return converters.stream()
                .filter(converter -> converter.supports(tagName))
                .findFirst();
    }

    /**
     * 判断是否支持指定的标签
     *
     * @param tagName HTML标签名（小写）
     * @return 是否支持该标签
     */
    public boolean supportsTag(String tagName) {
        return converters.stream()
                .anyMatch(converter -> converter.supports(tagName));
    }
}

package com.gls.athena.starter.word.generator;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Word文档生成器工厂
 *
 * @author athena
 */
@RequiredArgsConstructor
public class WordDocumentGeneratorFactory {

    private final List<WordDocumentGenerator> generators;
    private final WordProperties properties;

    /**
     * 获取适用于指定数据和注解的文档生成器
     *
     * @param data         数据对象
     * @param wordResponse 注解信息
     * @return 文档生成器
     */
    public WordDocumentGenerator getGenerator(Object data, WordResponse wordResponse) {
        Class<?> dataClass = data.getClass();

        // 优先级1: 用户通过注解显式指定的生成器
        if (wordResponse.generator() != void.class) {
            return findGeneratorByClass(wordResponse.generator(), dataClass);
        }

        // 优先级2: 基于模板路径的生成器选择
        if (StringUtils.hasText(wordResponse.template())) {
            return findTemplateBasedGenerator(dataClass);
        }

        // 优先级3: 基于数据类型的生成器选择
        return generators.stream()
                .filter(generator -> generator.supports(dataClass))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("没有找到支持数据类型[%s]的生成器", dataClass.getName())));
    }

    /**
     * 根据类类型查找生成器
     *
     * @param generatorClass 生成器类型
     * @param dataClass      数据类型
     * @return 找到的生成器
     */
    private WordDocumentGenerator findGeneratorByClass(Class<?> generatorClass, Class<?> dataClass) {
        return generators.stream()
                .filter(generator -> generator.getClass().equals(generatorClass) ||
                        generatorClass.isAssignableFrom(generator.getClass()))
                .filter(generator -> generator.supports(dataClass))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("指定的生成器类型[%s]不存在或不支持数据类型[%s]",
                                generatorClass.getName(), dataClass.getName())));
    }

    /**
     * 查找基于模板的生成器
     *
     * @param dataClass 数据类型
     * @return 找到的生成器
     */
    private WordDocumentGenerator findTemplateBasedGenerator(Class<?> dataClass) {
        // 首选POI-TL模板生成器
        WordDocumentGenerator poiTlGenerator = generators.stream()
                .filter(generator -> generator.getClass().getSimpleName().equals("PoiTlTemplateWordDocumentGenerator"))
                .filter(generator -> generator.supports(dataClass))
                .findFirst()
                .orElse(null);

        if (poiTlGenerator != null) {
            return poiTlGenerator;
        }

        // 降级使用普通模板生成器
        return generators.stream()
                .filter(generator -> generator.getClass().getSimpleName().equals("TemplateWordDocumentGenerator"))
                .filter(generator -> generator.supports(dataClass))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("没有找到支持模板且适用于数据类型[%s]的生成器", dataClass.getName())));
    }
}

package com.gls.athena.starter.word.generator;

import com.gls.athena.starter.file.generator.FileGenerator;
import com.gls.athena.starter.word.annotation.WordResponse;

/**
 * Word文档生成器接口，定义了Word导出功能的标准。
 * <p>
 * 实现类需根据注解配置和数据对象，生成Word文档并输出到指定流。
 * 支持多种生成方式（如模板、无模板等），可通过supports方法进行适配。
 * </p>
 *
 * @author george
 */
public interface WordGenerator extends FileGenerator<WordResponse> {

    /**
     * 判断是否支持当前注解配置。
     * <p>
     * 用于适配不同的生成器实现（如模板导出、无模板导出等）。
     * </p>
     *
     * @param wordResponse Word导出注解信息
     * @return 是否支持该配置
     */
    @Override
    default boolean supports(WordResponse wordResponse) {
        return wordResponse.generator().equals(this.getClass());
    }
}

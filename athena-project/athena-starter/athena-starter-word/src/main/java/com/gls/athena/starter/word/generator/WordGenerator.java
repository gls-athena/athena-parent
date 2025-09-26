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

}

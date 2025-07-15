package com.gls.athena.starter.word.generator;

import com.gls.athena.starter.word.annotation.WordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

/**
 * Word生成器管理服务，负责根据注解选择合适的Word生成器并生成Word文档。
 *
 * @author lizy19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WordGeneratorManager {

    /**
     * 注入所有实现了WordGenerator接口的生成器。
     */
    private final List<WordGenerator> generators;

    /**
     * 根据注解选择合适的生成器并生成Word文档。
     *
     * @param data         需要导出的数据
     * @param wordResponse Word导出注解信息
     * @param outputStream 输出流
     * @throws Exception 生成异常
     */
    public void generate(Object data, WordResponse wordResponse, OutputStream outputStream) throws Exception {
        WordGenerator generator = selectGenerator(wordResponse);
        generator.generate(data, wordResponse, outputStream);
    }

    /**
     * 根据注解选择支持的Word生成器。
     *
     * @param wordResponse Word导出注解信息
     * @return 支持的Word生成器
     */
    private WordGenerator selectGenerator(WordResponse wordResponse) {
        for (WordGenerator generator : generators) {
            if (generator.supports(wordResponse)) {
                log.debug("使用{}生成器", generator.getClass().getSimpleName());
                return generator;
            }
        }
        throw new RuntimeException("没有找到合适的Word生成器处理模板: " + wordResponse);
    }
}

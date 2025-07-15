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
     * 注入所有实现了WordGenerator接口的生成器实例。
     * <p>
     * 通过Spring自动注入，支持多种Word导出实现（如模板导出、无模板导出等）。
     * </p>
     */
    private final List<WordGenerator> generators;

    /**
     * 根据注解配置选择合适的生成器并生成Word文档。
     * <p>
     * 根据@WordResponse注解信息动态选择支持的WordGenerator实现，将数据导出为Word文档并写入输出流。
     * </p>
     *
     * @param data         需要导出的数据对象
     * @param wordResponse Word导出注解信息（包含模板、文件名等配置）
     * @param outputStream Word文档输出流
     * @throws Exception 生成或导出过程中发生的异常
     */
    public void generate(Object data, WordResponse wordResponse, OutputStream outputStream) throws Exception {
        WordGenerator generator = selectGenerator(wordResponse);
        generator.generate(data, wordResponse, outputStream);
    }

    /**
     * 根据注解配置选择支持的Word生成器实现。
     * <p>
     * 遍历所有已注册的WordGenerator，找到第一个支持当前@WordResponse配置的生成器。
     * </p>
     *
     * @param wordResponse Word导出注解信息
     * @return 支持该配置的Word生成器实例
     * @throws RuntimeException 未找到合适的生成器时抛出
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

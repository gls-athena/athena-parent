package com.gls.athena.starter.word.generator;

import com.gls.athena.starter.word.annotation.WordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;

/**
 * Word生成器管理服务。
 * <p>
 * 负责根据@WordResponse注解选择合适的WordGenerator实现，并生成Word文档。
 * 支持多种导出方式（如模板导出、无模板导出等）。
 * </p>
 *
 * @author lizy19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WordGeneratorManager {

    /**
     * 所有可用的WordGenerator实现，由Spring自动注入。
     */
    private final List<WordGenerator> generators;

    /**
     * 根据@WordResponse注解选择合适的WordGenerator并生成Word文档。
     *
     * @param data         控制器返回的数据对象
     * @param wordResponse Word导出注解信息
     * @param outputStream Word文档输出流
     * @throws Exception 生成Word文档时发生的异常
     */
    public void generate(Object data, WordResponse wordResponse, OutputStream outputStream) throws Exception {
        generators.stream()
                .filter(generator -> generator.supports(wordResponse))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到适配的WordGenerator实现"))
                .generate(data, wordResponse, outputStream);
    }

}

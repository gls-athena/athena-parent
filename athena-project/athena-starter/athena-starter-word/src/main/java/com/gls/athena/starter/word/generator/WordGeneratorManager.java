package com.gls.athena.starter.word.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

/**
 * Word生成器管理服务
 *
 * @author athena
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WordGeneratorManager {

    private final List<WordGenerator> generators;

    /**
     * 生成Word文档
     *
     * @param data         数据对象
     * @param template     模板路径
     * @param outputStream 输出流
     * @throws Exception 生成异常
     */
    public void generate(Object data, String template, OutputStream outputStream) throws Exception {
        WordGenerator generator = selectGenerator(template);
        generator.generate(data, template, outputStream);
    }

    /**
     * 选择合适的生成器
     *
     * @param template 模板路径
     * @return Word生成器
     */
    private WordGenerator selectGenerator(String template) {
        for (WordGenerator generator : generators) {
            if (generator.supports(template)) {
                log.debug("选择生成器: {} 处理模板: {}", generator.getClass().getSimpleName(), template);
                return generator;
            }
        }

        throw new RuntimeException("没有找到合适的Word生成器处理模板: " + template);
    }
}

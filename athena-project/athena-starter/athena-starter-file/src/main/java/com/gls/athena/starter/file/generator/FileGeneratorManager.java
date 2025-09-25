package com.gls.athena.starter.file.generator;

import lombok.RequiredArgsConstructor;

import java.io.OutputStream;
import java.util.List;

/**
 * 文件生成器管理器
 * 管理多个文件生成器，根据响应类型选择合适的文件生成器来生成文件
 *
 * @param <Response> 文件响应类型
 * @author george
 */
@RequiredArgsConstructor
public class FileGeneratorManager<Response> {

    /**
     * 文件生成器列表，包含所有可用的文件生成器实现
     */
    private final List<? extends FileGenerator<Response>> fileGenerators;

    /**
     * 根据数据和响应类型生成文件并写入输出流
     * 从文件生成器列表中查找支持指定响应类型的生成器，并使用该生成器生成文件
     *
     * @param data         用于生成文件的数据对象
     * @param response     文件响应对象，用于确定使用哪个文件生成器
     * @param outputStream 输出流，用于写入生成的文件内容
     * @throws Exception 当未找到适配的文件生成器或文件生成过程中出错时抛出异常
     */
    public void generate(Object data, Response response, OutputStream outputStream) throws Exception {
        fileGenerators.stream()
                .filter(generator -> generator.supports(response))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到适配的Generator实现"))
                .generate(data, response, outputStream);
    }
}

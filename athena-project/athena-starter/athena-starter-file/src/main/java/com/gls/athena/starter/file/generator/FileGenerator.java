package com.gls.athena.starter.file.generator;

import java.io.OutputStream;
import java.lang.annotation.Annotation;

/**
 * 文件生成器接口
 * 定义文件生成的基本操作，支持根据数据生成文件并判断是否支持特定的文件响应类型
 *
 * @param <Response> 文件响应类型
 * @author george
 */
public interface FileGenerator<Response extends Annotation> {

    /**
     * 根据数据生成文件并写入到输出流中
     *
     * @param data         用于生成文件的数据对象
     * @param response     文件响应对象，包含文件生成的相关配置信息
     * @param outputStream 输出流，用于写入生成的文件内容
     * @throws Exception 文件生成过程中可能抛出的异常
     */
    void generate(Object data, Response response, OutputStream outputStream) throws Exception;

    /**
     * 判断当前文件生成器是否支持指定的文件响应类型
     *
     * @param response 文件响应对象
     * @return 如果支持则返回true，否则返回false
     */
    default boolean supports(Response response) {
        return false;
    }
}

package com.gls.athena.starter.file.generator;

import cn.hutool.core.util.TypeUtil;
import com.gls.athena.starter.file.domain.FileResponseWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 文件生成器管理类，负责根据响应包装器查找并调用合适的文件生成器来生成文件内容。
 *
 * @author george
 */
@Slf4j
@Component
public class FileGeneratorManager {

    @Resource
    private List<? extends FileGenerator<?>> generators;

    /**
     * 根据文件响应包装器获取对应的文件生成器
     *
     * @param wrapper    文件响应包装器，包含具体的响应对象
     * @param <Response> 响应注解类型
     * @return 匹配的文件生成器实例
     * @throws IllegalArgumentException 当无法获取响应参数类型或未找到合适文件生成器时抛出异常
     */
    public <Response extends Annotation> FileGenerator<Response> getGenerator(FileResponseWrapper<Response> wrapper) {
        // 获取响应参数的实际类型
        Class<?> responseType = wrapper.response().annotationType();
        log.debug("responseType: {}", responseType);
        // 遍历所有文件生成器，查找匹配的生成器
        for (FileGenerator<?> generator : generators) {
            Type generatorType = TypeUtil.getTypeArgument(generator.getClass());
            log.debug("generatorType: {}", generatorType);
            if (generatorType != null && generatorType.equals(responseType)) {
                @SuppressWarnings("unchecked")
                FileGenerator<Response> wrapperGenerator = (FileGenerator<Response>) generator;
                // 检查生成器是否支持该响应对象
                if (wrapperGenerator.supports(wrapper.response()) || wrapper.supports(wrapperGenerator)) {
                    return wrapperGenerator;
                }
            }
        }

        throw new IllegalArgumentException("未找到合适的文件生成器，响应类型：" + responseType.getTypeName());
    }

    /**
     * 生成文件并输出到指定的输出流中
     *
     * @param data         要生成文件的数据对象
     * @param wrapper      文件响应包装器，包含响应注解信息
     * @param outputStream 输出流，用于写入生成的文件内容
     * @throws Exception 生成过程中可能抛出的异常
     */
    public <Response extends Annotation> void generate(Object data, FileResponseWrapper<Response> wrapper, OutputStream outputStream) throws Exception {
        // 获取对应的文件生成器
        FileGenerator<Response> generator = getGenerator(wrapper);
        // 调用生成器生成文件内容
        generator.generate(data, wrapper.response(), outputStream);
    }

}

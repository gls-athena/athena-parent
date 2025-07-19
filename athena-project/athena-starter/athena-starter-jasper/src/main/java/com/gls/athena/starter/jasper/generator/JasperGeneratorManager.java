package com.gls.athena.starter.jasper.generator;

import com.gls.athena.starter.jasper.annotation.JasperResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;

/**
 * Jasper报告生成管理器
 * 该类负责根据JasperResponse注解的类型来选择合适的JasperGenerator实现，并调用其generate方法生成报告
 *
 * @author george
 */
@Component
@RequiredArgsConstructor
public class JasperGeneratorManager {

    /**
     * 所有已注册的JasperGenerator实现
     * 这个列表用于在生成报告时选择合适的生成器
     */
    private final List<JasperGenerator> jasperGenerators;

    /**
     * 根据提供的数据和JasperResponse注解生成报告
     *
     * @param data           报告所需的数据，可以是任意对象，具体类型由实现类决定
     * @param jasperResponse JasperResponse注解，用于确定使用哪种类型的报告生成器
     * @param outputStream   输出流，用于接收生成的报告数据
     * @throws Exception 如果生成过程中发生错误，会抛出异常
     */
    public void generate(Object data, JasperResponse jasperResponse, OutputStream outputStream) throws Exception {
        // 根据JasperResponse找到合适的JasperGenerator实现
        jasperGenerators.stream()
                .filter(generator -> generator.supports(jasperResponse))
                .findFirst()
                // 如果找不到适配的JasperGenerator实现，抛出异常
                .orElseThrow(() -> new IllegalArgumentException("未找到适配的JasperGenerator实现"))
                // 调用找到的JasperGenerator实现的generate方法生成报告
                .generate(data, jasperResponse, outputStream);
    }
}

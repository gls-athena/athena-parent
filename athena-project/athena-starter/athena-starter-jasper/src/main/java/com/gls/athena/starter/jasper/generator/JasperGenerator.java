/**
 * Jasper报告生成器接口
 * 定义了生成报告和检查是否支持特定报告响应的合同
 */
package com.gls.athena.starter.jasper.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.common.core.util.FileUtil;
import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.jasper.config.JasperProperties;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * JasperGenerator接口用于定义生成Jasper报告所需的方法和默认方法
 *
 * @author george
 */
public interface JasperGenerator {

    /**
     * 生成Jasper报告的方法
     *
     * @param data           要填充到报告模板中的数据，可以是任何类型，具体取决于报告设计
     * @param jasperResponse Jasper报告响应注解，包含报告生成的配置信息
     * @param outputStream   输出流，用于接收生成的报告数据
     * @throws Exception 如果在生成报告过程中发生错误，则抛出异常
     */
    default void generate(Object data, JasperResponse jasperResponse, OutputStream outputStream) throws Exception {
        JasperProperties jasperProperties = SpringUtil.getBean(JasperProperties.class);
        // 加载报告模板
        InputStream template = FileUtil.getInputStream(jasperProperties.getTemplatePath(), jasperResponse.template());
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(template);

        // 将数据对象转换为Map，以便填充报告
        Map<String, Object> dataMap = BeanUtil.beanToMap(data);

        // 填充报告
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, dataMap, new JREmptyDataSource());

        // 导出报告
        exportReport(jasperPrint, outputStream);
    }

    /**
     * 导出Jasper报告到输出流
     *
     * @param jasperPrint  填充后的JasperPrint对象
     * @param outputStream 输出流，用于接收导出的报告数据
     * @throws JRException 如果导出过程中发生错误，则抛出JRException
     */
    void exportReport(JasperPrint jasperPrint, OutputStream outputStream) throws JRException;

    /**
     * 检查当前生成器是否支持给定的Jasper报告响应
     *
     * @param jasperResponse Jasper报告响应注解，用于检查是否支持
     * @return boolean 如果当前生成器的类与jasperResponse中指定的生成器类匹配，则返回true，否则返回false
     */
    default boolean supports(JasperResponse jasperResponse) {
        // 比较当前生成器类与注解中指定的生成器类是否相同
        return jasperResponse.generator().equals(this.getClass());
    }

}

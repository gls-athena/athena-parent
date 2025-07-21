package com.gls.athena.starter.word.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * 基于POI-TL的模板Word生成器
 *
 * @author athena
 */
@Slf4j
@Component
public class TemplateWordGenerator implements WordGenerator {

    @Resource
    private WordProperties wordProperties;

    /**
     * 根据模板和数据生成Word文档，支持自定义POI-TL配置。
     *
     * @param data         需要导出的数据对象
     * @param wordResponse Word导出注解信息，包含模板路径等配置
     * @param outputStream Word文档输出流
     * @throws Exception 生成或渲染过程中发生的异常
     */
    @Override
    public void generate(Object data, WordResponse wordResponse, OutputStream outputStream) throws Exception {
        String template = wordResponse.template();
        if (!StringUtils.hasText(template)) {
            throw new IllegalArgumentException("模板路径不能为空");
        }
        Map<String, Object> dataMap = BeanUtil.beanToMap(data);
        Configure configure = Configure.builder()
                .useSpringEL(false)
                .build();
        try (InputStream templateStream = getTemplateInputStream(template);
             XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateStream, configure)) {
            xwpfTemplate.render(dataMap);
            xwpfTemplate.write(outputStream);
        } catch (Exception e) {
            log.error("生成Word文档失败，模板: {}", template, e);
            throw new RuntimeException("生成Word文档失败", e);
        }
    }

    /**
     * 判断是否支持当前注解配置（即模板路径不为空）。
     *
     * @param wordResponse Word导出注解信息
     * @return 是否支持
     */
    @Override
    public boolean supports(WordResponse wordResponse) {
        return StrUtil.isNotBlank(wordResponse.template())
                && wordResponse.generator() == WordGenerator.class;
    }

    /**
     * 获取模板输入流，支持classpath路径。
     *
     * @param template 模板路径（支持classpath:前缀）
     * @return 模板输入流
     * @throws IOException 模板不存在或读取异常
     */
    private InputStream getTemplateInputStream(String template) throws IOException {
        return new ClassPathResource(getTemplatePath(template)).getInputStream();
    }

    /**
     * 获取模板的完整路径
     * <p>
     * 此方法首先从配置中获取模板路径，如果配置了模板路径，则将该路径与模板名称拼接起来
     * 如果未配置模板路径，则直接返回模板名称作为路径
     * 这样做的目的是为了支持既可以从指定路径也可以从默认路径加载模板
     *
     * @param template 模板名称
     * @return 模板的完整路径如果未配置路径，则返回模板名称
     */
    private String getTemplatePath(String template) {
        String templatePath = wordProperties.getTemplatePath();
        if (StringUtils.hasText(templatePath)) {
            // 如果配置了模板路径，则拼接模板路径
            return StrUtil.format("{}/{}", templatePath, template);
        } else {
            // 否则直接使用模板名称
            return template;
        }
    }
}

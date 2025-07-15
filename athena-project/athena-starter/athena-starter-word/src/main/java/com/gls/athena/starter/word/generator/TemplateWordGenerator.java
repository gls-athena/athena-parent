package com.gls.athena.starter.word.generator;

import cn.hutool.core.util.StrUtil;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.starter.word.annotation.WordResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取模板输入流，支持classpath路径。
     *
     * @param template 模板路径（支持classpath:前缀）
     * @return 模板输入流
     * @throws IOException 模板不存在或读取异常
     */
    private InputStream getTemplateInputStream(String template) throws IOException {
        Resource resource;
        if (StrUtil.isBlank(template)) {
            throw new IllegalArgumentException("模板路径不能为空");
        }
        if (template.startsWith("classpath:")) {
            resource = new ClassPathResource(template.substring("classpath:".length()));
        } else {
            resource = new ClassPathResource(template);
        }
        if (!resource.exists()) {
            throw new IllegalArgumentException("模板文件不存在: " + template);
        }
        return resource.getInputStream();
    }

    /**
     * 将数据对象转换为Map，便于模板渲染。
     *
     * @param data 数据对象
     * @return Map格式数据
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object data) {
        if (data == null) {
            return Map.of();
        }
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        return objectMapper.convertValue(data, Map.class);
    }

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
        Map<String, Object> dataMap = convertToMap(data);
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
        return StrUtil.isNotBlank(wordResponse.template());
    }
}

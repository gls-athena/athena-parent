package com.gls.athena.starter.word.generator;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Override
    public void generate(Object data, String template, OutputStream outputStream) throws Exception {
        if (!StringUtils.hasText(template)) {
            throw new IllegalArgumentException("模板路径不能为空");
        }

        // 转换数据为Map格式
        Map<String, Object> dataMap = convertToMap(data);

        // 配置POI-TL
        Configure configure = Configure.builder()
                .useSpringEL(false)
                .build();

        try (InputStream templateStream = getTemplateInputStream(template);
             XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateStream, configure)) {

            // 渲染模板
            xwpfTemplate.render(dataMap);

            // 输出到流
            xwpfTemplate.write(outputStream);

        } catch (Exception e) {
            log.error("生成Word文档失败，模板: {}", template, e);
            throw new RuntimeException("生成Word文档失败", e);
        }
    }

    @Override
    public boolean supports(String template) {
        return StringUtils.hasText(template) &&
                (template.endsWith(".docx") || template.endsWith(".doc"));
    }

    /**
     * 获取模板输入流
     */
    private InputStream getTemplateInputStream(String template) throws IOException {
        Resource resource;
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
     * 将数据对象转换为Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object data) {
        if (data == null) {
            return Map.of();
        }

        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }

        // 使用Jackson转换为Map
        return objectMapper.convertValue(data, Map.class);
    }
}

package com.gls.athena.starter.word.generator.impl;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordDocumentGenerator;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于POI-TL的Word文档模板生成器
 *
 * @author athena
 */
@Component
public class PoiTlTemplateWordDocumentGenerator implements WordDocumentGenerator {

    @Override
    public XWPFDocument generate(Object data, WordResponse wordResponse) {
        // 检查模板路径
        String templatePath = wordResponse.template();
        if (!StringUtils.hasText(templatePath)) {
            throw new IllegalArgumentException("Template path is required for template-based Word document");
        }

        try {
            // 准备数据模型
            Map<String, Object> dataModel = prepareDataModel(data);

            // 加载模板
            Resource templateResource = new ClassPathResource(templatePath);

            // 配置POI-TL
            Configure config = Configure.builder()
                    .useSpringEL(true)  // 使用SpEL表达式
                    .build();

            // 使用POI-TL渲染文档
            XWPFTemplate template = XWPFTemplate.compile(templateResource.getInputStream(), config)
                    .render(dataModel);

            // 将POI-TL文档转换为XWPFDocument
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            template.write(out);
            template.close();

            return new XWPFDocument(new ByteArrayInputStream(out.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Word document from template", e);
        }
    }

    @Override
    public boolean supports(Class<?> dataClass) {
        // 支持Map和POJO类型���数据
        return true;
    }

    /**
     * 准备数据模型
     *
     * @param data 原始数据
     * @return POI-TL数据模型
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> prepareDataModel(Object data) {
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        } else {
            // 对于非Map类型的数据，可以使用反射将其转换为Map
            // 这里简化处理，直接将对象放入"data"键下
            Map<String, Object> model = new HashMap<>();
            model.put("data", data);
            return model;
        }
    }
}

package com.gls.athena.starter.word.generator.impl;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordDocumentGenerator;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * 基于模板的Word文档生成器
 *
 * @author athena
 */
public class TemplateWordDocumentGenerator implements WordDocumentGenerator {

    @Override
    public XWPFDocument generate(Object data, WordResponse wordResponse) {
        // 检查模板路径
        String templatePath = wordResponse.template();
        if (!StringUtils.hasText(templatePath)) {
            throw new IllegalArgumentException("Template path is required for template-based Word document");
        }

        try {
            // 加载模板
            Resource templateResource = new ClassPathResource(templatePath);
            XWPFDocument document = new XWPFDocument(templateResource.getInputStream());

            // 使用数据填充模板
            if (data instanceof Map) {
                fillTemplate(document, (Map<String, Object>) data);
            } else {
                // 如果不是Map类型，可以将其转换为Map或使用其他方式处理
                throw new IllegalArgumentException("Template-based generator requires Map<String, Object> data");
            }

            return document;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Word document from template", e);
        }
    }

    @Override
    public boolean supports(Class<?> dataClass) {
        // 仅支持Map类型的数据
        return Map.class.isAssignableFrom(dataClass);
    }

    /**
     * 使用数据填充模板
     *
     * @param document 文档对象
     * @param data     数据Map
     */
    private void fillTemplate(XWPFDocument document, Map<String, Object> data) {
        // 遍历文档中的段落，替换占位符
        document.getParagraphs().forEach(paragraph -> {
            String text = paragraph.getText();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                if (text.contains(placeholder)) {
                    // 替换占位符
                    for (int i = 0; i < paragraph.getRuns().size(); i++) {
                        String runText = paragraph.getRuns().get(i).getText(0);
                        if (runText != null && runText.contains(placeholder)) {
                            paragraph.getRuns().get(i).setText(
                                    runText.replace(placeholder, entry.getValue().toString()), 0);
                        }
                    }
                }
            }
        });

        // 遍历表格中的单元格，替换占位符
        document.getTables().forEach(table -> {
            table.getRows().forEach(row -> {
                row.getTableCells().forEach(cell -> {
                    cell.getParagraphs().forEach(paragraph -> {
                        String text = paragraph.getText();
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            String placeholder = "${" + entry.getKey() + "}";
                            if (text.contains(placeholder)) {
                                // 替换占位符
                                for (int i = 0; i < paragraph.getRuns().size(); i++) {
                                    String runText = paragraph.getRuns().get(i).getText(0);
                                    if (runText != null && runText.contains(placeholder)) {
                                        paragraph.getRuns().get(i).setText(
                                                runText.replace(placeholder, entry.getValue().toString()), 0);
                                    }
                                }
                            }
                        }
                    });
                });
            });
        });
    }
}

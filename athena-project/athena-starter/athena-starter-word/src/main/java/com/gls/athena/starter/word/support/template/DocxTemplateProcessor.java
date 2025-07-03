package com.gls.athena.starter.word.support.template;

import cn.hutool.extra.template.TemplateConfig;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordProperties;
import com.gls.athena.starter.word.config.WordTemplateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Docx模板处理器
 * 负责处理docx格式的模板文件
 *
 * @author lizy19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocxTemplateProcessor implements TemplateProcessor {

    private final WordProperties wordProperties;

    @Override
    public void processTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {
        try {
            // 获取模板文件路径
            String templatePath = wordProperties.getTemplateConfig().getPath() + "/" + wordResponse.template();

            // 加载模板文件
            XWPFDocument document = loadDocxTemplate(templatePath);

            // 替换文档中的文本占位符
            replaceTextPlaceholders(document, data);

            // 替换文档中的表格占位符
            replaceTablePlaceholders(document, data);

            // 将处理后的文档写入输出流
            document.write(outputStream);

            log.info("成功处理DOCX模板: {}", wordResponse.template());
        } catch (Exception e) {
            log.error("处理DOCX模板失败: {}", wordResponse.template(), e);
            throw new RuntimeException("处理DOCX模板失败", e);
        }
    }

    @Override
    public boolean supports(WordTemplateType templateType) {
        return WordTemplateType.DOCX.equals(templateType);
    }

    /**
     * 从指定路径加载DOCX模板
     *
     * @param templatePath 模板路径
     * @return XWPFDocument对象
     * @throws IOException 如果加载失败
     */
    private XWPFDocument loadDocxTemplate(String templatePath) throws IOException {
        InputStream inputStream;
        // 根据模板配置的资源模式选择加载方式
        if (wordProperties.getTemplateConfig().getResourceMode().equals(TemplateConfig.ResourceMode.CLASSPATH)) {
            // 从类路径加载
            inputStream = getClass().getClassLoader().getResourceAsStream(templatePath);
            if (inputStream == null) {
                throw new FileNotFoundException("无法从类路径加载模板: " + templatePath);
            }
        } else {
            // 从文件系统加载
            inputStream = new FileInputStream(templatePath);
        }

        try {
            return new XWPFDocument(inputStream);
        } finally {
            // 确保输入流在使用后关闭
            inputStream.close();
        }
    }

    /**
     * 替换文档中的文本占位符
     *
     * @param document DOCX文档
     * @param data     数据映射
     */
    private void replaceTextPlaceholders(XWPFDocument document, Map<String, Object> data) {
        // 处理段落中的占位符
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceParagraphPlaceholders(paragraph, data);
        }

        // 处理页眉中的占位符
        for (XWPFHeader header : document.getHeaderList()) {
            for (XWPFParagraph paragraph : header.getParagraphs()) {
                replaceParagraphPlaceholders(paragraph, data);
            }
        }

        // 处理页脚中的占位符
        for (XWPFFooter footer : document.getFooterList()) {
            for (XWPFParagraph paragraph : footer.getParagraphs()) {
                replaceParagraphPlaceholders(paragraph, data);
            }
        }
    }

    /**
     * 替换段落中的占位符
     *
     * @param paragraph 段落对象
     * @param data      数据映射
     */
    private void replaceParagraphPlaceholders(XWPFParagraph paragraph, Map<String, Object> data) {
        // 获取段落的文本内容
        String paragraphText = paragraph.getText();
        // 检查段落文本中是否包含占位符
        if (paragraphText.contains("${")) {
            // 获取段落中的所有文本运行（run）
            List<XWPFRun> runs = paragraph.getRuns();
            if (runs != null) {
                // 遍历每个文本运行（run）
                for (XWPFRun run : runs) {
                    // 获取当前运行（run）的文本内容
                    String text = run.getText(0);
                    // 确保当前运行（run）的文本内容不为空
                    if (text != null) {
                        // 遍历数据映射，替换所有占位符
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            // 构造占位符字符串
                            String placeholder = "${" + entry.getKey() + "}";
                            // 检查当前运行（run）的文本内容中是否包含占位符
                            if (text.contains(placeholder)) {
                                // 获取占位符的替换值
                                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                                // 替换占位符为对应的值
                                text = text.replace(placeholder, value);
                            }
                        }
                        // 更新当前运行（run）的文本内容
                        run.setText(text, 0);
                    }
                }
            }
        }
    }

    /**
     * 替换表格中的占位符
     *
     * @param document DOCX文档
     * @param data     数据映射
     */
    private void replaceTablePlaceholders(XWPFDocument document, Map<String, Object> data) {
        // 遍历文档中的所有表格
        for (XWPFTable table : document.getTables()) {
            // 检查表格是否有动态行标记
            String dynamicRowKey = getDynamicRowKey(table);
            // 如果存在动态行标记且数据映射中包含该标记对应的列表，则处理动态表格
            if (dynamicRowKey != null && data.containsKey(dynamicRowKey) && data.get(dynamicRowKey) instanceof List) {
                // 处理动态表格
                processDynamicTable(table, dynamicRowKey, data);
            } else {
                // 处理静态表格，只替换占位符
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            // 替换段落中的占位符
                            replaceParagraphPlaceholders(paragraph, data);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取表格的动态行标记
     *
     * @param table 表格
     * @return 动态行数据的键，如果没有则返回null
     */
    private String getDynamicRowKey(XWPFTable table) {
        // 检查表格是否有足够的行
        if (table.getRows().isEmpty()) {
            return null;
        }

        // 只检查第一行的单元格
        XWPFTableRow firstRow = table.getRow(0);
        if (firstRow.getTableCells().isEmpty()) {
            return null;
        }

        // 优化：只检查第一个单元格，减少循环
        XWPFTableCell firstCell = firstRow.getTableCells().getFirst();
        String text = firstCell.getText();
        if (text != null && text.contains("${list:")) {
            int start = text.indexOf("${list:") + 7;
            int end = text.indexOf("}", start);
            if (end > start) {
                return text.substring(start, end);
            }
        }
        return null;
    }

    /**
     * 处理动态表格
     *
     * @param table         需���处理的表格
     * @param dynamicRowKey 动态行数据的键
     * @param data          数据映射
     */
    @SuppressWarnings("unchecked")
    private void processDynamicTable(XWPFTable table, String dynamicRowKey, Map<String, Object> data) {
        // 获取列表数据并进行类型和空值检查
        Object listObj = data.get(dynamicRowKey);
        if (!(listObj instanceof List)) {
            return;
        }

        List<Object> items = (List<Object>) listObj;
        if (items.isEmpty() || table.getRows().size() < 2) {
            return;
        }

        // 保存并预处理模板行
        XWPFTableRow templateRow = table.getRow(1);

        // 创建一个缓存来存储模板行的单元格结构，避免重复分析
        List<XWPFTableCell> templateCells = templateRow.getTableCells();
        int cellCount = templateCells.size();

        // 预先分配足够的行
        int itemsSize = items.size();

        // 处理每一个数据项
        for (int i = 0; i < itemsSize; i++) {
            Object item = items.get(i);
            if (!(item instanceof Map)) {
                continue;
            }

            Map<String, Object> rowData = (Map<String, Object>) item;

            // 复用模板行或创建新行
            XWPFTableRow currentRow;
            if (i == 0) {
                currentRow = templateRow;
            } else {
                currentRow = table.createRow();
                // 确保新行有足够的单元格
                while (currentRow.getTableCells().size() < cellCount) {
                    currentRow.createCell();
                }

                // 批量设置单元格属性，减少单元格级别的循环
                for (int j = 0; j < cellCount; j++) {
                    XWPFTableCell sourceCell = templateCells.get(j);
                    XWPFTableCell targetCell = currentRow.getTableCells().get(j);

                    // 设置单元格宽度和其他属性
                    targetCell.setWidth(String.valueOf(sourceCell.getWidth()));
                    if (sourceCell.getColor() != null) {
                        targetCell.setColor(sourceCell.getColor());
                    }

                    // 清除现有段落，准备填充新内容
                    targetCell.getParagraphs().clear();
                }
            }

            // 填充单元格内容
            fillRowWithData(currentRow, rowData, data, templateCells);
        }

        // 删除标记行
        table.removeRow(0);
    }

    /**
     * 填充行数据
     *
     * @param row           需要填充的行
     * @param rowData       行特定数据
     * @param globalData    全局数据
     * @param templateCells 模板单元格
     */
    private void fillRowWithData(XWPFTableRow row, Map<String, Object> rowData, Map<String, Object> globalData, List<XWPFTableCell> templateCells) {
        List<XWPFTableCell> cells = row.getTableCells();
        int cellCount = Math.min(cells.size(), templateCells.size());

        for (int i = 0; i < cellCount; i++) {
            XWPFTableCell targetCell = cells.get(i);
            XWPFTableCell templateCell = templateCells.get(i);

            // 复制模板单元格的段落到目标单元格
            for (XWPFParagraph templatePara : templateCell.getParagraphs()) {
                XWPFParagraph newPara = targetCell.addParagraph();

                // 复制段落属性
                if (templatePara.getCTP().isSetPPr()) {
                    newPara.getCTP().setPPr(templatePara.getCTP().getPPr());
                }

                // 处理运行并替换占位符
                for (XWPFRun templateRun : templatePara.getRuns()) {
                    XWPFRun newRun = newPara.createRun();
                    String text = templateRun.getText(0);

                    // 复制运行属性
                    if (templateRun.getCTR().isSetRPr()) {
                        newRun.getCTR().setRPr(templateRun.getCTR().getRPr());
                    }

                    // 替换文本中的占位符
                    if (text != null) {
                        // 优先替换行特定数据
                        text = replacePlaceholders(text, rowData);
                        // 然后替换全局数据
                        text = replacePlaceholders(text, globalData);
                        newRun.setText(text, 0);
                    }
                }
            }
        }
    }

    /**
     * 高效替换文本中的所有占位符
     *
     * @param text 包含占位符的文本
     * @param data 数据映射，键为占位符名称，值为替换后的值
     * @return 替换后的文本
     */
    private String replacePlaceholders(String text, Map<String, Object> data) {
        // 检查文本和数据映射是否为空，以及文本中是否包含占位符
        if (text == null || !text.contains("${") || data == null) {
            return text;
        }

        // 使用 StringBuilder 提高字符串操作的效率
        StringBuilder sb = new StringBuilder(text);
        // 遍历数据映射，进行占位符替换
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            // 构造占位符字符串
            String placeholder = "${" + entry.getKey() + "}";
            // 初始化占位符索引
            int placeholderIndex;
            // 获取占位符对应的值，如果为null，则替换为空字符串
            String value = entry.getValue() != null ? entry.getValue().toString() : "";

            // 使用 StringBuilder 进行高效替换
            while ((placeholderIndex = sb.indexOf(placeholder)) >= 0) {
                // 替换文本中的占位符为实际值
                sb.replace(placeholderIndex, placeholderIndex + placeholder.length(), value);
            }
        }
        // 返回替换后的文本
        return sb.toString();
    }
}

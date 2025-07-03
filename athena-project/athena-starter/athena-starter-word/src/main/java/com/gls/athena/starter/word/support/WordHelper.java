package com.gls.athena.starter.word.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateUtil;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordProperties;
import jakarta.annotation.Resource;
import org.apache.poi.xwpf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * word处理帮助类
 *
 * @author lizy19
 */
@Component
public class WordHelper {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]*)\\}");

    private static final int LIST_INDENTATION_LEFT = 720;
    @Resource
    private WordProperties wordProperties;

    /**
     * 处理docx模板并替换变量
     * <p>
     * 该方法接收一个数据映射、一个输出流和一个Word响应对象，用于替换模板中的变量
     * 它首先从模板路径加载文档，然后在文档的各个部分（段落、表格、页眉、页脚）中替换变量，
     * 最后将处理后的文档写入输出流
     *
     * @param data         包含变量及其对应值的映射，用于替换模板中的占位符
     * @param outputStream 用于输出处理后文档的流（调用方负责关闭）
     * @param wordResponse 包含模板路径的响应对象
     */
    public void handleDocxTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {
        ClassPathResource resource = new ClassPathResource(wordResponse.template());
        if (!resource.exists()) {
            throw new IllegalArgumentException("模板文件不存在: " + wordResponse.template());
        }

        try (InputStream is = resource.getInputStream()) {
            XWPFDocument document = new XWPFDocument(is);

            // 替换段落
            processParagraphs(document.getParagraphs(), data);

            // 替换表格
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        processParagraphs(cell.getParagraphs(), data);
                    }
                }
            }

            // 替换页眉
            for (XWPFHeader header : document.getHeaderList()) {
                processParagraphs(header.getParagraphs(), data);
                for (XWPFTable table : header.getTables()) {
                    for (XWPFTableRow row : table.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            processParagraphs(cell.getParagraphs(), data);
                        }
                    }
                }
            }

            // 替换页脚
            for (XWPFFooter footer : document.getFooterList()) {
                processParagraphs(footer.getParagraphs(), data);
                for (XWPFTable table : footer.getTables()) {
                    for (XWPFTableRow row : table.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            processParagraphs(cell.getParagraphs(), data);
                        }
                    }
                }
            }

            // 写入输出流
            document.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("处理Word模板失败", e);
        } catch (Exception e) {
            throw new RuntimeException("未知错误发生在Word模板处理过程中", e);
        }
    }

    /**
     * 对一组段落执行占位符替换
     */
    private void processParagraphs(List<XWPFParagraph> paragraphs, Map<String, Object> data) {
        for (XWPFParagraph paragraph : paragraphs) {
            replacePlaceholdersInParagraph(paragraph, data);
        }
    }

    /**
     * 处理HTML模板并转换为DOCX
     * <p>
     * 该方法使用给定的数据填充HTML模板，并将生成的HTML内容转换为DOCX格式的文件
     *
     * @param data         包含模板所需数据的映射，键是模板中的占位符，值是替换占位符的实际数据
     * @param outputStream 输出流，用于接收转换后的DOCX文件
     * @param wordResponse 包含模板信息的响应对象，用于获取模板内容和配置信息
     */
    public void handleHtmlTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {
        // 使用模板引擎根据数据渲染HTML内容
        String html = TemplateUtil.createEngine(wordProperties.getTemplateConfig())
                .getTemplate(wordResponse.template())
                .render(data);

        // 将渲染后的HTML内容转换为DOCX格式，并写入输出流
        htmlToDocx(html, outputStream);
    }

    /**
     * 将HTML转换为DOCX
     *
     * @param html         HTML字符串，代表要转换的文档内容
     * @param outputStream 输出流，用于保存转换后的DOCX文件
     */
    private void htmlToDocx(String html, OutputStream outputStream) {
        try {
            // 创建新的Word文档
            XWPFDocument document = new XWPFDocument();

            // 使用jsoup解析HTML
            org.jsoup.nodes.Document htmlDoc = Jsoup.parse(html);

            // 获取body元素内容
            Elements bodyElements = htmlDoc.body().children();

            // 遍历HTML元素并转换为Word元素
            for (Element element : bodyElements) {
                processElement(element, document);
            }

            // 写入输出流
            document.write(outputStream);
            document.close();
        } catch (Exception e) {
            // 异常处理，将内部异常包装为运行时异常
            throw new RuntimeException("HTML转换DOCX失败", e);
        }
    }

    /**
     * 处理HTML元素
     * 根据不同的HTML标签类型，将元素转换为对应的Word文档内容
     *
     * @param element  HTML元素，包含标签和内容
     * @param document Word文档对象，用于添加转换后的内容
     */
    private void processElement(Element element, XWPFDocument document) {
        // 获取元素的标签名，统一转换为小写以确保大小写不敏感
        String tagName = element.tagName().toLowerCase();

        // 根据标签名处理不同的HTML元素
        switch (tagName) {
            case "p":
                // 处理段落标签，创建一个新的Word段落，并处理其内容
                XWPFParagraph paragraph = document.createParagraph();
                processParagraphContent(element, paragraph);
                break;
            case "h1":
            case "h2":
            case "h3":
            case "h4":
            case "h5":
            case "h6":
                // 处理标题标签，创建一个新的Word段落，设置其样式为对应的标题级别，并处理文本内容
                XWPFParagraph heading = document.createParagraph();
                heading.setStyle(tagName.toUpperCase());
                XWPFRun headingRun = heading.createRun();
                headingRun.setText(element.text());
                headingRun.setBold(true);
                // 根据标题级别计算字体大小
                int fontSize = 20 - (Integer.parseInt(tagName.substring(1)) - 1) * 2;
                headingRun.setFontSize(fontSize);
                break;
            case "table":
                // 处理表格标签，调用专门的函数处理表格元素
                processTableElement(element, document);
                break;
            case "ul":
            case "ol":
                // 处理列表标签，调用专门的函数处理列表元素
                processListElement(element, document, tagName.equals("ol"));
                break;
            case "div":
                // 对于div，递归处理其子元素
                for (Element child : element.children()) {
                    processElement(child, document);
                }
                break;
            case "br":
                // 添加空段落作为换行
                document.createParagraph();
                break;
            case "hr":
                // 添加水平线
                XWPFParagraph hr = document.createParagraph();
                hr.setBorderBottom(Borders.BASIC_THIN_LINES);
                break;
            case "img":
                // 处理图片标签，调用专门的函数处理图片元素
                processImageElement(element, document);
                break;
            default:
                // 对于不识别的标签，递归处理其子元素
                for (Element child : element.children()) {
                    processElement(child, document);
                }
                break;
        }
    }

    /**
     * 处理段落内容，支持内联样式
     * 此方法负责将HTML元素转换为Word文档中的段落内容，包括文本和内联样式
     *
     * @param element   Jsoup解析的HTML元素，包含段落内容和内联样式
     * @param paragraph POI库中的XWPFParagraph对象，用于创建和管理Word文档中的段落
     */
    private void processParagraphContent(Element element, XWPFParagraph paragraph) {
        // 检查元素是否没有子元素，如果是，则直接添加文本到段落中
        if (element.children().isEmpty()) {
            // 没有子元素，直接添加文本
            XWPFRun run = paragraph.createRun();
            run.setText(element.text());
            return;
        }

        // 处理混合内容，即文本和内联样式同时存在的情况
        for (org.jsoup.nodes.Node node : element.childNodes()) {
            // 根据节点类型处理文本或内联样式
            if (node instanceof org.jsoup.nodes.TextNode) {
                // 处理文本节点
                String text = ((org.jsoup.nodes.TextNode) node).text();
                if (!text.trim().isEmpty()) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(text);
                }
            } else if (node instanceof Element childElement) {
                // 处理内联样式元素
                String tagName = childElement.tagName().toLowerCase();

                XWPFRun run = paragraph.createRun();
                run.setText(childElement.text());

                // 根据不同的HTML标签应用相应的样式
                switch (tagName) {
                    case "b":
                    case "strong":
                        run.setBold(true);
                        break;
                    case "i":
                    case "em":
                        run.setItalic(true);
                        break;
                    case "u":
                        run.setUnderline(UnderlinePatterns.SINGLE);
                        break;
                    case "s":
                    case "strike":
                    case "del":
                        run.setStrikeThrough(true);
                        break;
                    case "sub":
                        run.setSubscript(VerticalAlign.SUBSCRIPT);
                        break;
                    case "sup":
                        run.setSubscript(VerticalAlign.SUPERSCRIPT);
                        break;
                    default:
                        // 对于其他标签，直接设置文本
                        run.setText(childElement.text());
                        break;
                }

                // 处理颜色属性
                String color = childElement.attr("color");
                if (!color.isEmpty()) {
                    run.setColor(color.replace("#", ""));
                }

                // 处理字体大小属性
                String fontSize = childElement.attr("size");
                if (!fontSize.isEmpty()) {
                    try {
                        run.setFontSize(Integer.parseInt(fontSize));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
    }

    /**
     * 处理表格元素
     *
     * @param tableElement 表格的HTML元素
     * @param document     Word文档对象
     */
    private void processTableElement(Element tableElement, XWPFDocument document) {
        // 选择表格中的所有行
        Elements rows = tableElement.select("tr");
        if (rows.isEmpty()) {
            return;
        }

        // 确定列数
        int numCols = 0;
        for (Element row : rows) {
            numCols = Math.max(numCols, row.select("td, th").size());
        }

        if (numCols == 0) {
            return;
        }

        // 创建表格
        XWPFTable table = document.createTable(rows.size(), numCols);
        table.setWidth("100%");

        // 填充表格内容
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cells = row.select("td, th");

            for (int j = 0; j < cells.size(); j++) {
                Element cell = cells.get(j);
                XWPFTableCell tableCell = table.getRow(i).getCell(j);

                // 设置单元格内容
                for (Element childElement : cell.children()) {
                    XWPFParagraph cellParagraph = tableCell.getParagraphArray(0);
                    if (cellParagraph == null) {
                        cellParagraph = tableCell.addParagraph();
                    }
                    processParagraphContent(childElement, cellParagraph);
                }

                // 如果没有子元素，直接设置文本
                if (cell.children().isEmpty()) {
                    tableCell.setText(cell.text());
                }

                // 表头单元格应用粗体
                if (cell.tagName().equalsIgnoreCase("th")) {
                    for (XWPFParagraph p : tableCell.getParagraphs()) {
                        for (XWPFRun run : p.getRuns()) {
                            run.setBold(true);
                        }
                    }
                }
            }
        }
    }

    private void processListElement(Element listElement, XWPFDocument document, boolean isOrdered) {
        Elements items = listElement.select("li");
        for (Element element : items) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setIndentationLeft(LIST_INDENTATION_LEFT);
            paragraph.setNumID(getOrCreateNumbering(document, isOrdered));

            if (element.children().isEmpty()) {
                String text = element.text();
                if (!text.isEmpty()) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(text);
                }
            } else {
                processParagraphContent(element, paragraph);
            }
        }
    }

    /**
     * 获取或创建编号样式
     * 此方法用于根据文档和顺序需求获取或创建一个编号样式
     * 如果是有序的编号，则返回1；否则返回2
     * 实际应用中可能需要更复杂的逻辑来处理编号的创建和获取
     *
     * @param document  文档对象，用于获取或创建编号样式
     * @param isOrdered 指示是否需要有序编号的布尔值
     * @return 返回编号样式的标识
     */
    private BigInteger getOrCreateNumbering(XWPFDocument document, boolean isOrdered) {
        // 简化实现，实际应用中可能需要更复杂的逻辑
        return BigInteger.valueOf(isOrdered ? 1 : 2);
    }

    /**
     * 处理图片元素（简化实现）
     * <p>
     * 该方法负责处理HTML中的图片元素，将其转换为文档中的描述性文本。由于实际的图片文件处理较为复杂，
     * 这里采用了一种简化的处理方式，即用文本占位来表示图片。
     *
     * @param imgElement 图片元素，从中提取图片的alt属性作为描述性文本
     * @param document   XWPFDocument对象，用于创建新的段落和文本
     */
    private void processImageElement(Element imgElement, XWPFDocument document) {
        if (imgElement == null) {
            // 可选：记录警告日志，便于调试
            return;
        }

        String altText = imgElement.attr("alt");
        if (StrUtil.isBlank(altText)) {
            altText = "无描述";
        }

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("[图片: " + altText + "]");
    }

    /**
     * 替换段落中的占位符
     * 该方法用于在给定的段落中替换预定义的占位符
     * 占位符在文档中以特定格式标记，例如 ${key}
     * 这些占位符将被Map中对应的值替换
     *
     * @param paragraph 要处理的段落对象
     * @param data      包含占位符键值对的数据Map
     */
    private void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, Object> data) {
        // 获取段落的文本内容
        String paragraphText = paragraph.getText();
        // 使用正则表达式匹配文本中的占位符
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(paragraphText);

        // 检查是否有占位符存在
        if (matcher.find()) {
            // 有占位符，重置matcher
            matcher.reset();

            // 清除现有runs，我们将创建新的包含替换后内容的run
            List<XWPFRun> runs = paragraph.getRuns();
            int size = runs.size();
            for (int i = size - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            // 创建包含替换后内容的新run
            XWPFRun newRun = paragraph.createRun();
            StringBuffer sb = new StringBuffer();

            // 遍历所有匹配的占位符并替换
            while (matcher.find()) {
                // 提取占位符中的键
                String key = matcher.group(1);
                // 从数据Map中获取对应的值
                Object value = data.get(key);
                // 构造替换字符串，如果值为空，则用空字符串替换
                String replacement = value != null ? value.toString() : "";
                // 使用 Matcher.quoteReplacement 防止 $ 和 \ 导致异常
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            // 将剩余的文本追加到StringBuffer中
            matcher.appendTail(sb);

            // 将替换后的文本设置到新的run中
            newRun.setText(sb.toString());
        }
    }

}
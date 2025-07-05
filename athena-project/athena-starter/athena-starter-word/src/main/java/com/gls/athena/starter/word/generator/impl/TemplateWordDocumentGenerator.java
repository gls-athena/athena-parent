package com.gls.athena.starter.word.generator.impl;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordDocumentGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于模板的Word文档生成器
 * 优化版本：支持更好的性能和更多功能
 *
 * @author athena
 */
@Slf4j
@Component
public class TemplateWordDocumentGenerator implements WordDocumentGenerator {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    private static final Pattern CONDITIONAL_PATTERN = Pattern.compile("\\$\\{if:([^}]+)}([\\s\\S]*?)\\$\\{/if}");
    private static final Pattern LOOP_PATTERN = Pattern.compile("\\$\\{foreach:([^}]+)}([\\s\\S]*?)\\$\\{/foreach}");
    private static final Pattern MATH_PATTERN = Pattern.compile("\\$\\{math:([^}]+)}");
    private static final String DEFAULT_NULL_VALUE = "";

    /**
     * 缓存字段反射信息，提升性能
     */
    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 预编译的日期时间格式化器
     */
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
            if (!templateResource.exists()) {
                throw new IllegalArgumentException("Template file not found: " + templatePath);
            }

            XWPFDocument document = new XWPFDocument(templateResource.getInputStream());
            log.info("Successfully loaded template: {}", templatePath);

            // 转换数据为Map格式
            Map<String, Object> dataMap = convertToMap(data);

            // 添加系统内置变量
            addSystemVariables(dataMap);

            // 使用优化的方法填充模板
            fillTemplateOptimized(document, dataMap);

            log.info("Template filled successfully with {} data entries", dataMap.size());
            return document;
        } catch (IOException e) {
            log.error("Failed to generate Word document from template: {}", templatePath, e);
            throw new RuntimeException("Failed to generate Word document from template", e);
        } catch (Exception e) {
            log.error("Unexpected error during document generation", e);
            throw new RuntimeException("Unexpected error during document generation", e);
        }
    }

    @Override
    public boolean supports(Class<?> dataClass) {
        // 支持Map类型和普通Java对象
        return Map.class.isAssignableFrom(dataClass) || !dataClass.isPrimitive();
    }

    /**
     * 添加系统内置变量
     *
     * @param dataMap 数据Map
     */
    private void addSystemVariables(Map<String, Object> dataMap) {
        LocalDateTime now = LocalDateTime.now();
        dataMap.put("currentDate", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dataMap.put("currentTime", now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        dataMap.put("currentDateTime", now.format(DEFAULT_DATE_FORMATTER));
        dataMap.put("timestamp", System.currentTimeMillis());
    }

    /**
     * 将数据对象转换为Map
     *
     * @param data 数据对象
     * @return Map格式的数据
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object data) {
        if (data == null) {
            return new HashMap<>(16);
        }

        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }

        // 使用缓存优化的反射将对象转换为Map
        Class<?> clazz = data.getClass();
        Map<String, Field> fieldMap = getFieldMap(clazz);
        Map<String, Object> result = new HashMap<>(fieldMap.size());

        try {
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                Field field = entry.getValue();
                Object value = field.get(data);

                // 处理嵌套对象
                if (value != null && !isPrimitiveOrWrapper(value.getClass()) &&
                        !(value instanceof String) && !(value instanceof Date) &&
                        !(value instanceof Collection) && !(value instanceof Map)) {
                    // 递归转换嵌套对象
                    Map<String, Object> nestedMap = convertToMap(value);
                    result.put(entry.getKey(), nestedMap);
                } else {
                    result.put(entry.getKey(), value);
                }
            }
        } catch (IllegalAccessException e) {
            log.warn("Failed to convert object to map using reflection, using toString instead", e);
            result.clear();
            result.put("data", data.toString());
        }

        return result;
    }

    /**
     * 获取类的字段映射（使用缓存）
     *
     * @param clazz 类对象
     * @return 字段映射
     */
    private Map<String, Field> getFieldMap(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, this::buildFieldMap);
    }

    /**
     * 构建字段映射
     *
     * @param clazz 类对象
     * @return 字段映射
     */
    private Map<String, Field> buildFieldMap(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Field> fieldMap = new HashMap<>(fields.length);

        for (Field field : fields) {
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        }

        return fieldMap;
    }

    /**
     * 判断是否为基本类型或包装类型
     *
     * @param clazz 类对象
     * @return 是否为基本类型或包装类型
     */
    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Boolean.class || clazz == Character.class ||
                clazz == Byte.class || clazz == Short.class ||
                clazz == Integer.class || clazz == Long.class ||
                clazz == Float.class || clazz == Double.class;
    }

    /**
     * 优化的模板填充方法
     *
     * @param document 文档对象
     * @param data     数据Map
     */
    private void fillTemplateOptimized(XWPFDocument document, Map<String, Object> data) {
        // 批量填充主体内容
        List<XWPFParagraph> allParagraphs = new ArrayList<>(document.getParagraphs());
        List<XWPFTable> allTables = new ArrayList<>(document.getTables());

        // 处理页眉页脚
        for (XWPFHeader header : document.getHeaderList()) {
            allParagraphs.addAll(header.getParagraphs());
            allTables.addAll(header.getTables());
        }

        for (XWPFFooter footer : document.getFooterList()) {
            allParagraphs.addAll(footer.getParagraphs());
            allTables.addAll(footer.getTables());
        }

        // 批量处理段落
        fillParagraphs(allParagraphs, data);

        // 批量处理表格
        fillTables(allTables, data);
    }

    /**
     * 填充段落列表
     *
     * @param paragraphs 段落列表
     * @param data       数据Map
     */
    private void fillParagraphs(List<XWPFParagraph> paragraphs, Map<String, Object> data) {
        for (XWPFParagraph paragraph : paragraphs) {
            fillParagraph(paragraph, data);
        }
    }

    /**
     * 优化的段落填充方法
     *
     * @param paragraph 段落对象
     * @param data      数据Map
     */
    private void fillParagraph(XWPFParagraph paragraph, Map<String, Object> data) {
        String fullText = getParagraphText(paragraph);
        if (!StringUtils.hasText(fullText)) {
            return;
        }

        // 检查是否包含占位符
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(fullText);
        if (!matcher.find()) {
            return;
        }

        // 构建新文本，保留原有格式
        String newText = replacePlaceholders(fullText, data);

        // 保留第一个run的格式，清除其他runs
        List<XWPFRun> runs = paragraph.getRuns();
        if (!runs.isEmpty()) {
            XWPFRun firstRun = runs.getFirst();

            // 清除所有runs
            clearParagraphRuns(paragraph);

            // 创建新的run并复制格式
            XWPFRun newRun = paragraph.createRun();
            copyRunFormat(firstRun, newRun);
            newRun.setText(newText);
        } else {
            // 如果没有run，直接创建新的
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(newText);
        }
    }

    /**
     * 获取段落的完整文本（处理跨run的占位符）
     *
     * @param paragraph 段落对象
     * @return 完整文本
     */
    private String getParagraphText(XWPFParagraph paragraph) {
        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                fullText.append(text);
            }
        }
        return fullText.toString();
    }

    /**
     * 替换文本中的占位符（增强版，支持条件和数学运算）
     *
     * @param text 原始文本
     * @param data 数据Map
     * @return 替换后的文本
     */
    private String replacePlaceholders(String text, Map<String, Object> data) {
        // 首先处理条件渲染
        text = processConditionalBlocks(text, data);

        // 处理循环
        text = processLoopBlocks(text, data);

        // 处理数学运算
        text = processMathExpressions(text, data);

        // 最后处理普通占位符
        return processSimplePlaceholders(text, data);
    }

    /**
     * 处理条件渲染块 ${if:condition}content${/if}
     *
     * @param text 文本
     * @param data 数据
     * @return 处理后的文本
     */
    private String processConditionalBlocks(String text, Map<String, Object> data) {
        Matcher matcher = CONDITIONAL_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            String condition = matcher.group(1).trim();
            String content = matcher.group(2);

            // 添加条件块前的文本
            result.append(text, lastEnd, matcher.start());

            // 评估条件
            if (evaluateCondition(condition, data)) {
                result.append(content);
            }

            lastEnd = matcher.end();
        }

        result.append(text.substring(lastEnd));
        return result.toString();
    }

    /**
     * 处理循环块 ${foreach:items}content${/foreach}
     *
     * @param text 文本
     * @param data 数据
     * @return 处理后的文本
     */
    private String processLoopBlocks(String text, Map<String, Object> data) {
        Matcher matcher = LOOP_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            String listKey = matcher.group(1).trim();
            String template = matcher.group(2);

            // 添加循环块前的文本
            result.append(text, lastEnd, matcher.start());

            // 处理循环
            Object listData = getNestedValue(data, listKey);
            if (listData instanceof Collection) {
                Collection<?> items = (Collection<?>) listData;
                int index = 0;
                for (Object item : items) {
                    Map<String, Object> itemData = new HashMap<>(data);
                    itemData.put("item", item);
                    itemData.put("index", index);
                    itemData.put("isFirst", index == 0);
                    itemData.put("isLast", index == items.size() - 1);

                    // 如果item是对象，将其属性添加到数据中
                    if (item != null && !(item instanceof String) && !isPrimitiveOrWrapper(item.getClass())) {
                        Map<String, Object> itemFields = convertToMap(item);
                        itemData.putAll(itemFields);
                    }

                    // 递归处理模板内容
                    String processedTemplate = replacePlaceholders(template, itemData);
                    result.append(processedTemplate);
                    index++;
                }
            }

            lastEnd = matcher.end();
        }

        result.append(text.substring(lastEnd));
        return result.toString();
    }

    /**
     * 处理数学表达式 ${math:expression}
     *
     * @param text 文本
     * @param data 数据
     * @return 处理后的文本
     */
    private String processMathExpressions(String text, Map<String, Object> data) {
        Matcher matcher = MATH_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            String expression = matcher.group(1).trim();

            // 添加数学表达式前的文本
            result.append(text, lastEnd, matcher.start());

            // 计算数学表达式
            try {
                double value = evaluateMathExpression(expression, data);
                // 如果是整数，显示为整数
                if (value == (long) value) {
                    result.append((long) value);
                } else {
                    result.append(String.format("%.2f", value));
                }
            } catch (Exception e) {
                log.warn("Failed to evaluate math expression: {}", expression, e);
                result.append("NaN");
            }

            lastEnd = matcher.end();
        }

        result.append(text.substring(lastEnd));
        return result.toString();
    }

    /**
     * 处理简单占位符
     *
     * @param text 文本
     * @param data 数据
     * @return 处理后的文本
     */
    private String processSimplePlaceholders(String text, Map<String, Object> data) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            String key = matcher.group(1);

            // 跳过已处理的特殊占位符
            if (key.startsWith("if:") || key.startsWith("foreach:") ||
                    key.startsWith("math:") || key.equals("/if") || key.equals("/foreach")) {
                result.append(text, lastEnd, matcher.end());
                lastEnd = matcher.end();
                continue;
            }

            // 添加占位符前的文本
            result.append(text, lastEnd, matcher.start());

            // 处理格式化指令，支持 ${key:format} 格式
            String formatSpec = null;
            if (key.contains(":")) {
                String[] parts = key.split(":", 2);
                key = parts[0];
                formatSpec = parts[1];
            }

            // 替换占位符
            Object value = getNestedValue(data, key);
            String replacement = formatValueWithSpec(value, formatSpec);
            result.append(replacement);

            lastEnd = matcher.end();
        }

        // 添加剩余文本
        result.append(text.substring(lastEnd));
        return result.toString();
    }

    /**
     * 评估条件表达式
     *
     * @param condition 条件表达式
     * @param data      数据
     * @return 条件结果
     */
    private boolean evaluateCondition(String condition, Map<String, Object> data) {
        try {
            // 处理简单的条件表达式
            if (condition.contains("==")) {
                String[] parts = condition.split("==", 2);
                Object left = getConditionValue(parts[0].trim(), data);
                Object right = getConditionValue(parts[1].trim(), data);
                return Objects.equals(left, right);
            } else if (condition.contains("!=")) {
                String[] parts = condition.split("!=", 2);
                Object left = getConditionValue(parts[0].trim(), data);
                Object right = getConditionValue(parts[1].trim(), data);
                return !Objects.equals(left, right);
            } else if (condition.contains(">=")) {
                String[] parts = condition.split(">=", 2);
                return compareNumbers(parts[0].trim(), parts[1].trim(), data) >= 0;
            } else if (condition.contains("<=")) {
                String[] parts = condition.split("<=", 2);
                return compareNumbers(parts[0].trim(), parts[1].trim(), data) <= 0;
            } else if (condition.contains(">")) {
                String[] parts = condition.split(">", 2);
                return compareNumbers(parts[0].trim(), parts[1].trim(), data) > 0;
            } else if (condition.contains("<")) {
                String[] parts = condition.split("<", 2);
                return compareNumbers(parts[0].trim(), parts[1].trim(), data) < 0;
            } else {
                // 简单的布尔值检查
                Object value = getNestedValue(data, condition);
                return isTruthy(value);
            }
        } catch (Exception e) {
            log.debug("Failed to evaluate condition: {}", condition, e);
            return false;
        }
    }

    /**
     * 获取条件值
     *
     * @param expr 表达式
     * @param data 数据
     * @return 值
     */
    private Object getConditionValue(String expr, Map<String, Object> data) {
        // 处理字符串字面量
        if ((expr.startsWith("'") && expr.endsWith("'")) ||
                (expr.startsWith("\"") && expr.endsWith("\""))) {
            return expr.substring(1, expr.length() - 1);
        }

        // 处理数字字面量
        try {
            if (expr.contains(".")) {
                return Double.parseDouble(expr);
            } else {
                return Long.parseLong(expr);
            }
        } catch (NumberFormatException e) {
            // 不是数字，当作变量名处理
        }

        // 处理布尔字面量
        if ("true".equals(expr)) return true;
        if ("false".equals(expr)) return false;
        if ("null".equals(expr)) return null;

        // 当作变量名处理
        return getNestedValue(data, expr);
    }

    /**
     * 比较数字
     *
     * @param left  左值表达式
     * @param right 右值表达式
     * @param data  数据
     * @return 比较结果
     */
    private int compareNumbers(String left, String right, Map<String, Object> data) {
        Object leftVal = getConditionValue(left, data);
        Object rightVal = getConditionValue(right, data);

        if (leftVal instanceof Number && rightVal instanceof Number) {
            double leftNum = ((Number) leftVal).doubleValue();
            double rightNum = ((Number) rightVal).doubleValue();
            return Double.compare(leftNum, rightNum);
        }

        throw new IllegalArgumentException("Cannot compare non-numeric values");
    }

    /**
     * 判断值是否为真
     *
     * @param value 值
     * @return 是否为真
     */
    private boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        if (value instanceof String) return !((String) value).isEmpty();
        if (value instanceof Collection) return !((Collection<?>) value).isEmpty();
        return true;
    }

    /**
     * 计算数学表达式
     *
     * @param expression 数学表达式
     * @param data       数据
     * @return 计算结果
     */
    private double evaluateMathExpression(String expression, Map<String, Object> data) {
        // 替换变量
        String processedExpr = expression;
        Matcher matcher = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_\\.\\[\\]]*)").matcher(expression);

        while (matcher.find()) {
            String varName = matcher.group(1);
            Object value = getNestedValue(data, varName);
            if (value instanceof Number) {
                processedExpr = processedExpr.replace(varName, value.toString());
            }
        }

        // 简单的数学表达式计算器
        return evaluateSimpleMath(processedExpr);
    }

    /**
     * 简单的数学表达式计算器
     *
     * @param expression 表达式
     * @return 计算结果
     */
    private double evaluateSimpleMath(String expression) {
        // 移除空格
        expression = expression.replaceAll("\\s+", "");

        // 处理括号
        while (expression.contains("(")) {
            int start = expression.lastIndexOf('(');
            int end = expression.indexOf(')', start);
            if (end == -1) throw new IllegalArgumentException("Mismatched parentheses");

            String subExpr = expression.substring(start + 1, end);
            double result = evaluateSimpleMath(subExpr);
            expression = expression.substring(0, start) + result + expression.substring(end + 1);
        }

        // 处理加减法
        if (expression.contains("+") || expression.contains("-")) {
            for (int i = expression.length() - 1; i >= 0; i--) {
                char c = expression.charAt(i);
                if ((c == '+' || c == '-') && i > 0) {
                    String left = expression.substring(0, i);
                    String right = expression.substring(i + 1);
                    if (c == '+') {
                        return evaluateSimpleMath(left) + evaluateSimpleMath(right);
                    } else {
                        return evaluateSimpleMath(left) - evaluateSimpleMath(right);
                    }
                }
            }
        }

        // 处理乘除法
        if (expression.contains("*") || expression.contains("/")) {
            for (int i = expression.length() - 1; i >= 0; i--) {
                char c = expression.charAt(i);
                if (c == '*' || c == '/') {
                    String left = expression.substring(0, i);
                    String right = expression.substring(i + 1);
                    if (c == '*') {
                        return evaluateSimpleMath(left) * evaluateSimpleMath(right);
                    } else {
                        double rightVal = evaluateSimpleMath(right);
                        if (rightVal == 0) throw new ArithmeticException("Division by zero");
                        return evaluateSimpleMath(left) / rightVal;
                    }
                }
            }
        }

        // 解析数字
        try {
            return Double.parseDouble(expression);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + expression);
        }
    }

    /**
     * 根据格式规范格式化值
     *
     * @param value      值
     * @param formatSpec 格式规范
     * @return 格式化后的字符串
     */
    private String formatValueWithSpec(Object value, String formatSpec) {
        if (value == null) {
            return DEFAULT_NULL_VALUE;
        }

        if (formatSpec == null) {
            return formatValue(value);
        }

        try {
            return switch (formatSpec.toLowerCase()) {
                case "date" -> {
                    if (value instanceof Date) {
                        yield DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
                                ((Date) value).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                    } else if (value instanceof LocalDateTime) {
                        yield ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                    yield value.toString();
                }
                case "time" -> {
                    if (value instanceof Date) {
                        yield DateTimeFormatter.ofPattern("HH:mm:ss").format(
                                ((Date) value).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime());
                    } else if (value instanceof LocalDateTime) {
                        yield ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    }
                    yield value.toString();
                }
                case "upper" -> value.toString().toUpperCase();
                case "lower" -> value.toString().toLowerCase();
                case "currency" -> {
                    if (value instanceof Number) {
                        yield String.format("¥%.2f", ((Number) value).doubleValue());
                    }
                    yield value.toString();
                }
                case "percent" -> {
                    if (value instanceof Number) {
                        yield String.format("%.1f%%", ((Number) value).doubleValue() * 100);
                    }
                    yield value.toString();
                }
                default -> {
                    // 尝试作为日期格式模式
                    if (value instanceof Date) {
                        yield DateTimeFormatter.ofPattern(formatSpec).format(
                                ((Date) value).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                    } else if (value instanceof LocalDateTime) {
                        yield ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(formatSpec));
                    } else if (value instanceof Number && formatSpec.startsWith("%.")) {
                        // 数字格式化
                        yield String.format(formatSpec, ((Number) value).doubleValue());
                    }
                    yield value.toString();
                }
            };
        } catch (Exception e) {
            log.debug("Failed to format value with spec {}: {}", formatSpec, e.getMessage());
            return value.toString();
        }
    }

    /**
     * 格式化值
     *
     * @param value 值对象
     * @return 格式化后的字符串
     */
    private String formatValue(Object value) {
        return switch (value) {
            case null -> DEFAULT_NULL_VALUE;
            case Date date -> DEFAULT_DATE_FORMATTER.format(date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            case LocalDateTime localDateTime -> localDateTime.format(DEFAULT_DATE_FORMATTER);
            default -> value.toString();
        };
    }

    /**
     * 复制run的格式
     *
     * @param source 源run
     * @param target 目标run
     */
    private void copyRunFormat(XWPFRun source, XWPFRun target) {
        if (source.getFontFamily() != null) {
            target.setFontFamily(source.getFontFamily());
        }
        // 使用新的API替代已弃用的方法
        if (source.getFontSizeAsDouble() != null) {
            target.setFontSize(source.getFontSizeAsDouble());
        }
        target.setBold(source.isBold());
        target.setItalic(source.isItalic());
        target.setUnderline(source.getUnderline());
        target.setStrikeThrough(source.isStrikeThrough());
        if (source.getColor() != null) {
            target.setColor(source.getColor());
        }
    }

    /**
     * 填充表格
     *
     * @param tables 表格列表
     * @param data   数据Map
     */
    private void fillTables(List<XWPFTable> tables, Map<String, Object> data) {
        for (XWPFTable table : tables) {
            fillTable(table, data);
        }
    }

    /**
     * 填充单个表格
     *
     * @param table 表格对象
     * @param data  数据Map
     */
    private void fillTable(XWPFTable table, Map<String, Object> data) {
        List<XWPFTableRow> rows = table.getRows();
        if (rows.isEmpty()) {
            return;
        }

        // 检查是否为动态表格（包含列表数据）
        if (isDynamicTable(table, data)) {
            fillDynamicTable(table, data);
        } else {
            // 普通表格填充
            fillStaticTable(table, data);
        }
    }

    /**
     * 判断是否为动态表格
     *
     * @param table 表格对象
     * @param data  数据Map
     * @return 是否为动态表格
     */
    private boolean isDynamicTable(XWPFTable table, Map<String, Object> data) {
        // 查找表格中是否包含列表占位符模式 ${list:dataKey}
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                String cellText = getCellText(cell);
                if (cellText.contains("${list:")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 填充动态表格（支持列表数据）
     *
     * @param table 表格对象
     * @param data  数据Map
     */
    private void fillDynamicTable(XWPFTable table, Map<String, Object> data) {
        List<XWPFTableRow> rows = table.getRows();
        XWPFTableRow templateRow = null;
        String listKey = null;
        int templateRowIndex = -1;

        // 查找模板行
        for (int i = 0; i < rows.size(); i++) {
            XWPFTableRow row = rows.get(i);
            String rowText = getRowText(row);
            Pattern listPattern = Pattern.compile("\\$\\{list:([^}]+)}");
            Matcher matcher = listPattern.matcher(rowText);
            if (matcher.find()) {
                templateRow = row;
                listKey = matcher.group(1);
                templateRowIndex = i;
                break;
            }
        }

        if (templateRow == null || listKey == null) {
            // 没有找到列表模板，按普通表格处理
            fillStaticTable(table, data);
            return;
        }

        // 获取列表数据
        Object listData = getNestedValue(data, listKey);
        if (!(listData instanceof Collection<?> items)) {
            log.warn("List key '{}' does not point to a Collection", listKey);
            return;
        }

        // 清除原模板行中的列表占位符
        clearListPlaceholders(templateRow);

        // 为每个数据项创建新行
        int currentIndex = templateRowIndex;
        for (Object item : items) {
            XWPFTableRow newRow;
            if (currentIndex == templateRowIndex) {
                // 第一行使用原模板行
                newRow = templateRow;
            } else {
                // 创建新行
                newRow = table.insertNewTableRow(currentIndex);
                copyRowStructure(templateRow, newRow);
            }

            // 填充行数据
            Map<String, Object> itemData = new HashMap<>(data);
            if (item instanceof Map) {
                itemData.putAll((Map<String, Object>) item);
            } else {
                itemData.put("item", item);
                // 将对象属性添加到数据中
                Map<String, Object> itemFields = convertToMap(item);
                itemData.putAll(itemFields);
            }

            fillTableRow(newRow, itemData);
            currentIndex++;
        }

        // 如果没有数据，移除模板行
        if (items.isEmpty()) {
            table.removeRow(templateRowIndex);
        }
    }

    /**
     * 填充静态表格
     *
     * @param table 表格对象
     * @param data  数据Map
     */
    private void fillStaticTable(XWPFTable table, Map<String, Object> data) {
        for (XWPFTableRow row : table.getRows()) {
            fillTableRow(row, data);
        }
    }

    /**
     * 填充表格行
     *
     * @param row  表格行
     * @param data 数据Map
     */
    private void fillTableRow(XWPFTableRow row, Map<String, Object> data) {
        for (XWPFTableCell cell : row.getTableCells()) {
            fillParagraphs(cell.getParagraphs(), data);
        }
    }

    /**
     * 获取单元格文本
     *
     * @param cell 单元格
     * @return 文本内容
     */
    private String getCellText(XWPFTableCell cell) {
        StringBuilder text = new StringBuilder();
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            text.append(getParagraphText(paragraph));
        }
        return text.toString();
    }

    /**
     * 获取行文本
     *
     * @param row 表格行
     * @return 文本内容
     */
    private String getRowText(XWPFTableRow row) {
        StringBuilder text = new StringBuilder();
        for (XWPFTableCell cell : row.getTableCells()) {
            text.append(getCellText(cell));
        }
        return text.toString();
    }

    /**
     * 清除列表占位符
     *
     * @param row 表格行
     */
    private void clearListPlaceholders(XWPFTableRow row) {
        for (XWPFTableCell cell : row.getTableCells()) {
            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                String text = getParagraphText(paragraph);
                // 移除 ${list:key} 占位符
                String cleanText = text.replaceAll("\\$\\{list:[^}]+}", "");
                if (!text.equals(cleanText)) {
                    clearParagraphRuns(paragraph);
                    if (!cleanText.trim().isEmpty()) {
                        XWPFRun newRun = paragraph.createRun();
                        newRun.setText(cleanText);
                    }
                }
            }
        }
    }

    /**
     * 复制行结构
     *
     * @param sourceRow 源行
     * @param targetRow 目标行
     */
    private void copyRowStructure(XWPFTableRow sourceRow, XWPFTableRow targetRow) {
        List<XWPFTableCell> sourceCells = sourceRow.getTableCells();

        // 确保目标行有足够的单元格
        while (targetRow.getTableCells().size() < sourceCells.size()) {
            targetRow.createCell();
        }

        List<XWPFTableCell> targetCells = targetRow.getTableCells();

        for (int i = 0; i < sourceCells.size() && i < targetCells.size(); i++) {
            XWPFTableCell sourceCell = sourceCells.get(i);
            XWPFTableCell targetCell = targetCells.get(i);

            // 复制单元格内容和格式
            copyCellContent(sourceCell, targetCell);
        }
    }

    /**
     * 复制单元格内容
     *
     * @param sourceCell 源单元格
     * @param targetCell 目标单元格
     */
    private void copyCellContent(XWPFTableCell sourceCell, XWPFTableCell targetCell) {
        // 清除目标单元格现有内容
        targetCell.removeParagraph(0);

        // 复制段落
        for (XWPFParagraph sourceParagraph : sourceCell.getParagraphs()) {
            XWPFParagraph targetParagraph = targetCell.addParagraph();
            copyParagraphContent(sourceParagraph, targetParagraph);
        }
    }

    /**
     * 复制段落内容
     *
     * @param source 源段落
     * @param target 目标段落
     */
    private void copyParagraphContent(XWPFParagraph source, XWPFParagraph target) {
        // 复制段落级别的格式
        target.setAlignment(source.getAlignment());
        target.setSpacingBetween(source.getSpacingBetween());

        // 复制runs
        for (XWPFRun sourceRun : source.getRuns()) {
            XWPFRun targetRun = target.createRun();
            copyRunFormat(sourceRun, targetRun);

            String text = sourceRun.getText(0);
            if (text != null) {
                targetRun.setText(text);
            }
        }
    }

    /**
     * 获取嵌套属性值，支持点号分隔的路径和数组索引
     *
     * @param data 数据Map
     * @param key  属性键，支持嵌套如 "user.name" 或 "users[0].name"
     * @return 属性值
     */
    private Object getNestedValue(Map<String, Object> data, String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }

        // 处理数组索引和点号分隔的复合路径
        String[] parts = parseKeyPath(key);
        Object current = data;

        for (String part : parts) {
            if (current == null) {
                return null;
            }

            // 检查是否为数组访问
            if (part.contains("[") && part.endsWith("]")) {
                current = handleArrayAccess(current, part);
            } else if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                // 尝试通过反射获取属性值
                current = getFieldValue(current, part);
                if (current == null) {
                    log.debug("Failed to get nested value for key: {}", key);
                    return null;
                }
            }
        }

        return current;
    }

    /**
     * 解析键路径，支持数组索引
     *
     * @param key 键路径
     * @return 路径部分数组
     */
    private String[] parseKeyPath(String key) {
        // 简单的路径分割，支持 user.addresses[0].street 这样的格式
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inBracket = false;

        for (char c : key.toCharArray()) {
            if (c == '.' && !inBracket) {
                if (!current.isEmpty()) {
                    parts.add(current.toString());
                    current.setLength(0);
                }
            } else {
                if (c == '[') {
                    inBracket = true;
                } else if (c == ']') {
                    inBracket = false;
                }
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            parts.add(current.toString());
        }

        return parts.toArray(new String[0]);
    }

    /**
     * 处理数组访问
     *
     * @param obj  对象
     * @param part 包含数组索引的部分，如 "items[0]"
     * @return 数组元素
     */
    private Object handleArrayAccess(Object obj, String part) {
        int bracketStart = part.indexOf('[');
        int bracketEnd = part.indexOf(']');

        if (bracketStart == -1 || bracketEnd == -1 || bracketEnd <= bracketStart) {
            return null;
        }

        String fieldName = part.substring(0, bracketStart);
        String indexStr = part.substring(bracketStart + 1, bracketEnd);

        try {
            int index = Integer.parseInt(indexStr);

            // 获取数组或列表字段
            Object arrayField;
            if (obj instanceof Map) {
                arrayField = ((Map<?, ?>) obj).get(fieldName);
            } else {
                arrayField = getFieldValue(obj, fieldName);
            }

            if (arrayField instanceof List) {
                List<?> list = (List<?>) arrayField;
                return (index >= 0 && index < list.size()) ? list.get(index) : null;
            } else if (arrayField instanceof Object[]) {
                Object[] array = (Object[]) arrayField;
                return (index >= 0 && index < array.length) ? array[index] : null;
            }
        } catch (NumberFormatException e) {
            log.debug("Invalid array index: {}", indexStr);
        }

        return null;
    }

    /**
     * 通过反射获取字段值，增强版本支持getter方法
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @return 字段值
     */
    private Object getFieldValue(Object obj, String fieldName) {
        try {
            // 首先尝试直接字段访问
            Map<String, Field> fieldMap = getFieldMap(obj.getClass());
            Field field = fieldMap.get(fieldName);
            if (field != null) {
                return field.get(obj);
            }

            // 如果字段不存在，尝试getter方法
            String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            try {
                var method = obj.getClass().getMethod(getterName);
                return method.invoke(obj);
            } catch (Exception e) {
                // 尝试boolean类型的is方法
                String isGetterName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                try {
                    var isMethod = obj.getClass().getMethod(isGetterName);
                    return isMethod.invoke(obj);
                } catch (Exception ex) {
                    log.debug("Failed to get field value via getter: {}.{}", obj.getClass().getSimpleName(), fieldName);
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get field value: {}.{}", obj.getClass().getSimpleName(), fieldName, e);
        }
        return null;
    }

    /**
     * 清除段落中的所有runs
     *
     * @param paragraph 段落对象
     */
    private void clearParagraphRuns(XWPFParagraph paragraph) {
        // 从后往前删除，避免索引问题
        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
    }
}


package com.gls.athena.starter.word.processor.impl;

import com.gls.athena.starter.word.processor.PlaceholderProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 条件占位符处理器
 * 处理 ${if:condition}content${/if} 格式的条件渲染
 *
 * @author athena
 */
@Slf4j
@Component
public class ConditionalProcessor implements PlaceholderProcessor {

    private static final Pattern CONDITIONAL_PATTERN = Pattern.compile("\\$\\{if:([^}]+)}([\\s\\S]*?)\\$\\{/if}");

    @Override
    public boolean supports(String placeholder) {
        return placeholder.startsWith("if:");
    }

    @Override
    public String process(String placeholder, Map<String, Object> data) {
        Matcher matcher = CONDITIONAL_PATTERN.matcher("${" + placeholder + "}");
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            String condition = matcher.group(1).trim();
            String content = matcher.group(2);

            result.append(placeholder, lastEnd, matcher.start());

            if (evaluateCondition(condition, data)) {
                result.append(content);
            }

            lastEnd = matcher.end();
        }

        result.append(placeholder.substring(lastEnd));
        return result.toString();
    }

    @Override
    public int getPriority() {
        return 10;
    }

    private boolean evaluateCondition(String condition, Map<String, Object> data) {
        try {
            // AND 操作符
            if (condition.contains(" && ")) {
                String[] parts = condition.split(" && ");
                for (String part : parts) {
                    if (!evaluateCondition(part.trim(), data)) {
                        return false;
                    }
                }
                return true;
            }

            // OR 操作符
            if (condition.contains(" || ")) {
                String[] parts = condition.split(" \\|\\| ");
                for (String part : parts) {
                    if (evaluateCondition(part.trim(), data)) {
                        return true;
                    }
                }
                return false;
            }

            // NOT 操作符
            if (condition.startsWith("!")) {
                return !evaluateCondition(condition.substring(1).trim(), data);
            }

            // 等于操作符
            if (condition.contains("==")) {
                String[] parts = condition.split("==", 2);
                Object left = getConditionValue(parts[0].trim(), data);
                Object right = getConditionValue(parts[1].trim(), data);
                return java.util.Objects.equals(left, right);
            }

            // 不等于操作符
            if (condition.contains("!=")) {
                String[] parts = condition.split("!=", 2);
                Object left = getConditionValue(parts[0].trim(), data);
                Object right = getConditionValue(parts[1].trim(), data);
                return !java.util.Objects.equals(left, right);
            }

            // 简单的布尔值检查
            Object value = getNestedValue(data, condition);
            return isTruthy(value);

        } catch (Exception e) {
            log.debug("Failed to evaluate condition: {}", condition, e);
            return false;
        }
    }

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

        return getNestedValue(data, expr);
    }

    private Object getNestedValue(Map<String, Object> data, String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }

        String[] parts = key.split("\\.");
        Object current = data;

        for (String part : parts) {
            if (current == null) {
                return null;
            }

            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                // 反射获取字段值
                try {
                    java.lang.reflect.Field field = current.getClass().getDeclaredField(part);
                    field.setAccessible(true);
                    current = field.get(current);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        return current;
    }

    private boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        if (value instanceof String) return !((String) value).isEmpty();
        if (value instanceof java.util.Collection) return !((java.util.Collection<?>) value).isEmpty();
        return true;
    }
}

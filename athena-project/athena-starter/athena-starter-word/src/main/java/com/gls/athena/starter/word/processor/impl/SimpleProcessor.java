package com.gls.athena.starter.word.processor.impl;

import com.gls.athena.starter.word.processor.PlaceholderProcessor;
import com.gls.athena.starter.word.formatter.ValueFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 简单占位符处理器
 * 处理普通的 ${key} 和 ${key:format} 格式的占位符
 *
 * @author athena
 */
@Slf4j
@Component
public class SimpleProcessor implements PlaceholderProcessor {

    @Autowired
    private ValueFormatter valueFormatter;

    @Override
    public boolean supports(String placeholder) {
        // 简单占位符不包含特殊前缀
        return !placeholder.startsWith("if:") &&
               !placeholder.startsWith("foreach:") &&
               !placeholder.startsWith("math:") &&
               !placeholder.startsWith("include:") &&
               !placeholder.equals("/if") &&
               !placeholder.equals("/foreach");
    }

    @Override
    public String process(String placeholder, Map<String, Object> data) {
        String key = placeholder;
        String formatSpec = null;

        // 处理格式化指令 ${key:format}
        if (placeholder.contains(":")) {
            String[] parts = placeholder.split(":", 2);
            key = parts[0];
            formatSpec = parts[1];
        }

        Object value = getNestedValue(data, key);
        return valueFormatter.format(value, formatSpec);
    }

    @Override
    public int getPriority() {
        return 100; // 最低优先级，作为默认处理器
    }

    private Object getNestedValue(Map<String, Object> data, String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }

        String[] parts = parseKeyPath(key);
        Object current = data;

        for (String part : parts) {
            if (current == null) {
                return null;
            }

            current = getValueFromObject(current, part);
        }

        return current;
    }

    private String[] parseKeyPath(String key) {
        java.util.List<String> parts = new java.util.ArrayList<>();
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

    private Object getValueFromObject(Object obj, String key) {
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).get(key);
        }

        // 处理数组索引 users[0]
        if (key.contains("[") && key.contains("]")) {
            String arrayKey = key.substring(0, key.indexOf('['));
            String indexStr = key.substring(key.indexOf('[') + 1, key.indexOf(']'));

            try {
                int index = Integer.parseInt(indexStr);
                Object arrayObj = getFieldValue(obj, arrayKey);

                if (arrayObj instanceof java.util.List) {
                    java.util.List<?> list = (java.util.List<?>) arrayObj;
                    return index < list.size() ? list.get(index) : null;
                } else if (arrayObj instanceof Object[]) {
                    Object[] array = (Object[]) arrayObj;
                    return index < array.length ? array[index] : null;
                }
            } catch (NumberFormatException e) {
                log.debug("Invalid array index: {}", indexStr);
            }
            return null;
        }

        return getFieldValue(obj, key);
    }

    private Object getFieldValue(Object obj, String fieldName) {
        try {
            // 尝试直接字段访问
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            // 尝试getter方法
            try {
                String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                java.lang.reflect.Method getter = obj.getClass().getMethod(getterName);
                return getter.invoke(obj);
            } catch (Exception ex) {
                // 尝试boolean类型的is方法
                try {
                    String isGetterName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                    java.lang.reflect.Method isGetter = obj.getClass().getMethod(isGetterName);
                    return isGetter.invoke(obj);
                } catch (Exception exc) {
                    log.debug("Failed to get field value: {}.{}", obj.getClass().getSimpleName(), fieldName);
                    return null;
                }
            }
        }
    }
}

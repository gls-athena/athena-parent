package com.gls.athena.starter.word.processor.impl;

import com.gls.athena.starter.word.processor.PlaceholderProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 循环占位符处理器
 * 处理 ${foreach:items}content${/foreach} 格式的循环渲染
 *
 * @author athena
 */
@Slf4j
@Component
public class LoopProcessor implements PlaceholderProcessor {

    private static final Pattern LOOP_PATTERN = Pattern.compile("\\$\\{foreach:([^}]+)}([\\s\\S]*?)\\$\\{/foreach}");

    @Override
    public boolean supports(String placeholder) {
        return placeholder.startsWith("foreach:");
    }

    @Override
    public String process(String placeholder, Map<String, Object> data) {
        Matcher matcher = LOOP_PATTERN.matcher("${" + placeholder + "}");
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            String listKey = matcher.group(1).trim();
            String template = matcher.group(2);

            result.append(placeholder, lastEnd, matcher.start());

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

                    result.append(template);
                    index++;
                }
            }

            lastEnd = matcher.end();
        }

        result.append(placeholder.substring(lastEnd));
        return result.toString();
    }

    @Override
    public int getPriority() {
        return 20;
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object data) {
        if (data == null) {
            return new HashMap<>();
        }

        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }

        Map<String, Object> result = new HashMap<>();
        try {
            java.lang.reflect.Field[] fields = data.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(data);
                result.put(field.getName(), value);
            }
        } catch (IllegalAccessException e) {
            log.warn("Failed to convert object to map", e);
        }

        return result;
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Boolean.class || clazz == Character.class ||
                clazz == Byte.class || clazz == Short.class ||
                clazz == Integer.class || clazz == Long.class ||
                clazz == Float.class || clazz == Double.class;
    }
}

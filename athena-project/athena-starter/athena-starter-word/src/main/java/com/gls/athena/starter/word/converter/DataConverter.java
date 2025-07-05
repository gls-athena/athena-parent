package com.gls.athena.starter.word.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据转换器
 * 负责将各种类型的数据转换为Map格式，并添加系统变量
 *
 * @author athena
 */
@Slf4j
@Component
public class DataConverter {

    /**
     * 缓存字段反射信息，提升性能
     */
    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 预编译的日期时间格式化器
     */
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 转换数据为Map格式并添加系统变量
     *
     * @param data 原始数据
     * @return 转换后的Map
     */
    public Map<String, Object> convertAndEnrich(Object data) {
        Map<String, Object> result = convertToMap(data);
        addSystemVariables(result);
        return result;
    }

    /**
     * 将数据对象转换为Map
     *
     * @param data 数据对象
     * @return Map格式的数据
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> convertToMap(Object data) {
        if (data == null) {
            return new HashMap<>();
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
     * 添加系统内置变量
     *
     * @param dataMap 数据Map
     */
    public void addSystemVariables(Map<String, Object> dataMap) {
        LocalDateTime now = LocalDateTime.now();
        dataMap.put("currentDate", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dataMap.put("currentTime", now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        dataMap.put("currentDateTime", now.format(DEFAULT_DATE_FORMATTER));
        dataMap.put("timestamp", System.currentTimeMillis());
        dataMap.put("uuid", UUID.randomUUID().toString());
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
}

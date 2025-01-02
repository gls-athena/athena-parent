package com.gls.athena.sdk.amap.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.QueryMapEncoder;

import java.util.Map;

/**
 * Feign 查询参数映射编码器的 JSON 实现
 * <p>
 * 该实现通过 Jackson 将对象转换为 Map 结构：
 * 1. 首先将对象序列化为 JSON 字符串
 * 2. 然后将 JSON 字符串反序列化为 Map 结构
 * <p>
 * 主要用于处理复杂对象到 HTTP 查询参数的转换
 *
 * @author george
 */
public class JsonQueryMapEncoder implements QueryMapEncoder {

    /**
     * Jackson 的 ObjectMapper 实例
     * 用于处理 JSON 序列化和反序列化
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象编码为查询参数 Map
     *
     * @param object 需要转换的源对象
     * @return 包含键值对的 Map，用于构建 HTTP 查询参数
     * @throws RuntimeException 当 JSON 处理过程中发生异常时抛出
     */
    @Override
    public Map<String, Object> encode(Object object) {
        try {
            // 将对象序列化为 JSON 字符串
            String json = objectMapper.writeValueAsString(object);
            // 将 JSON 字符串反序列化为 Map<String, Object> 结构
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
